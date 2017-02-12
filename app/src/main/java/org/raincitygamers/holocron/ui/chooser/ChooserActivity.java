package org.raincitygamers.holocron.ui.chooser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.chooser.pages.basics.BasicsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.characteristics.CharacteristicsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.description.DescriptionChooser;
import org.raincitygamers.holocron.ui.chooser.pages.done.DoneChooser;
import org.raincitygamers.holocron.ui.chooser.pages.force.ForceChooser;
import org.raincitygamers.holocron.ui.chooser.pages.skills.CombatSkillsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.skills.GeneralSkillsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.skills.KnowledgeSkillsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.talents.TalentsChooser;
import org.raincitygamers.holocron.ui.display.DisplayActivity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ChooserActivity extends ActivityBase implements ContentPage.OnFragmentInteractionListener {
  public static final String ACTION_FINISH = ChooserActivity.class.getCanonicalName();
  private ListView drawerList;
  private DrawerLayout drawerLayout;
  private ArrayAdapter<String> adapter;
  private ActionBarDrawerToggle drawerToggle;
  @NotNull
  @Getter
  private final Character activeCharacter = Character.of();
  private int currentPage = -1;
  private FinishReceiver finishReceiver;
  @Getter
  @Setter
  private boolean chooserDone = false;

  private static final String LOG_TAG = DisplayActivity.class.getSimpleName();
  private final List<ContentPage> contentPages = new ArrayList<>();

  public ChooserActivity() {
    // This is where we populate what shows up in the menu.
    // If it's white, I need to add an empty constructor.
    contentPages.add(new BasicsChooser());
    contentPages.add(new CharacteristicsChooser());
    contentPages.add(new GeneralSkillsChooser());
    contentPages.add(new CombatSkillsChooser());
    contentPages.add(new KnowledgeSkillsChooser());
    contentPages.add(new TalentsChooser());
    contentPages.add(new ForceChooser());
    contentPages.add(new DescriptionChooser());
    contentPages.add(new DoneChooser());
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_character);

    finishReceiver = new FinishReceiver();
    registerReceiver(finishReceiver, new IntentFilter(ACTION_FINISH));

    drawerList = (ListView) findViewById(R.id.navList);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    addDrawerItems();
    setUpDrawer();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    selectPage(0);
    setTitle();
  }

  @Override
  protected void onStop() {
    if (chooserDone) {
      unregisterReceiver(finishReceiver);
    }

    super.onStop();
  }

  private void addDrawerItems() {
    List<String> pageNames = new ArrayList<>();
    for (ContentPage page : contentPages) {
      pageNames.add(page.getTitle());
    }

    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pageNames);
    drawerList.setAdapter(adapter);

    drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectPage(position);
      }
    });
  }

  private void setUpDrawer() {
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
      /** Called when a drawer has settled in a completely open state. */
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        getSupportActionBar().setTitle("Switch Page");
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }

      /** Called when a drawer has settled in a completely closed state. */
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        setTitle();
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }
    };

    drawerToggle.setDrawerIndicatorEnabled(true);
    drawerLayout.setDrawerListener(drawerToggle);
  }

  private void selectPage(int pageNumber) {
    Fragment displayPage = contentPages.get(pageNumber);
    FragmentManager fragmentManager = getFragmentManager();
    if (pageNumber != currentPage) {
      currentPage = pageNumber;
      fragmentManager.beginTransaction().replace(R.id.content_frame, displayPage).commit();
    }

    drawerLayout.closeDrawer(drawerList);
  }

  private void setTitle() {
    String title = contentPages.get(currentPage).getTitle();
    getSupportActionBar().setTitle(title);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    // Activate the navigation drawer toggle
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onNavFragmentInteraction(Uri uri) {
    Log.i(LOG_TAG, "onNavFragmentInteraction called");
  }

  private final class FinishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_FINISH)) {
        finish();
      }
    }
  }
}
