package org.raincitygamers.holocron.ui.display;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.creation.CreationActivity;
import org.raincitygamers.holocron.ui.display.stats.StatsTab;
import org.raincitygamers.holocron.ui.display.description.DescriptionTab;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

public class DisplayCharacterActivity extends AppCompatActivity implements DescriptionTab.OnFragmentInteractionListener {
  private static final String LOG_TAG = DisplayCharacterActivity.class.getSimpleName();
  private static final CharacterManager characterManager = CharacterManager.getInstance();
  private static final long THIRTY_SECONDS = 30000;
  @Getter private Character character;
  private Timer timer;

  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;

  private List<DisplayCharacterFragment> displayFragments = Arrays.asList(new StatsTab(), new DescriptionTab());

  @Override
  protected void onPause() {
    super.onPause();

    timer.cancel();
    Log.i(LOG_TAG, "Save character: " + character.getName());
    characterManager.saveCharacter(character);
  }

  @Override
  protected void onResume() {
    super.onResume();
    autoSaveCharacter();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display_character);
    sendBroadcast(new Intent(CreationActivity.ACTION_FINISH));

    // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    // setSupportActionBar(toolbar);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);

    try {
      character = characterManager.getActiveCharacter();
      Log.i(LOG_TAG, "Character: " + character);
    }
    catch (IllegalStateException e) {
      // TODO: Return to the main activity.
    }
  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      return displayFragments.get(position);
    }

    @Override
    public int getCount() {
      return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
      case 0:
        return "Basics";
      case 1:
        return "Skills";
      }
      return null;
    }
  }

  private void autoSaveCharacter() {
    final Handler handler = new Handler();
    timer = new Timer();
    TimerTask saveCharacter = new TimerTask() {
      @Override
      public void run() {
        handler.post(new Runnable() {
          public void run() {
            Log.i(LOG_TAG, "Autosave: " + character.getName());
            characterManager.saveCharacter(character);
          }
        });
      }
    };

    timer.scheduleAtFixedRate(saveCharacter, THIRTY_SECONDS, THIRTY_SECONDS);
  }
}
