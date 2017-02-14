package org.raincitygamers.holocron.ui.display;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;
import org.raincitygamers.holocron.ui.display.pages.abilities.ForcePowersPage;
import org.raincitygamers.holocron.ui.display.pages.abilities.TalentsPage;
import org.raincitygamers.holocron.ui.display.pages.basics.BasicsPage;
import org.raincitygamers.holocron.ui.display.pages.description.DescriptionPage;
import org.raincitygamers.holocron.ui.display.pages.gear.GearPage;
import org.raincitygamers.holocron.ui.display.pages.skills.CombatSkillsPage;
import org.raincitygamers.holocron.ui.display.pages.skills.GeneralSkillsPage;
import org.raincitygamers.holocron.ui.display.pages.skills.KnowledgeSkillsPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends ActivityBase implements ContentPage.OnFragmentInteractionListener {
  private ListView drawerList;
  private DrawerLayout drawerLayout;
  private ArrayAdapter<String> adapter;
  private ActionBarDrawerToggle drawerToggle;
  private Character activeCharacter;
  private int currentPageNumber = 0;

  private static final String LOG_TAG = DisplayActivity.class.getSimpleName();
  private static final long THIRTY_SECONDS = 3000;
  private Timer timer;

  private final List<ContentPage> contentPages = new ArrayList<>();
  private final List<DrawerCommand> otherDrawerCommands = new ArrayList<>();

  public DisplayActivity() {
    activeCharacter = CharacterManager.getActiveCharacter();
    // This is where we populate what shows up in the menu.
    // If it's white, I need to add an empty constructor.
    contentPages.add(new BasicsPage());
    // contentPages.add(new ActionsPage());
    contentPages.add(new GeneralSkillsPage());
    contentPages.add(new CombatSkillsPage());
    contentPages.add(new KnowledgeSkillsPage());
    contentPages.add(new GearPage());
    contentPages.add(new TalentsPage());
    contentPages.add(new DescriptionPage());
    if (activeCharacter.getForceRating() > 0) {
      contentPages.add(new ForcePowersPage());
    }

    otherDrawerCommands.add(new DrawerCommand("Edit", new CommandAction() {
      @Override public void act() {
        drawerLayout.closeDrawer(drawerList);
        CharacterManager.setActiveCharacter(activeCharacter);
        CharacterManager.saveCharacter(activeCharacter);
        Intent intent = new Intent(DisplayActivity.this, ChooserActivity.class);
        intent.putExtra(EDIT_ACTIVE_CHARACTER, true);
        intent.putExtra(CURRENT_OPEN_PAGE, contentPages.get(currentPageNumber).getTitle());
        startActivity(intent);
      }
    }));
  }

  @Override
  protected void onPause() {
    super.onPause();

    timer.cancel();
    CharacterManager.saveActiveCharacter();
  }

  @Override
  protected void onResume() {
    super.onResume();
    autoSaveCharacter();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_character);
    sendBroadcast(new Intent(ChooserActivity.ACTION_FINISH));

    drawerList = (ListView) findViewById(R.id.navList);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    addDrawerItems();
    setUpDrawer();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    selectPage(activeCharacter.getLastOpenPage());
    setTitle();
  }

  private void addDrawerItems() {
    List<String> drawerEntries = new ArrayList<>();
    for (ContentPage page : contentPages) {
      drawerEntries.add(page.getTitle());
    }

    for (DrawerCommand command : otherDrawerCommands) {
      drawerEntries.add(command.getLabel());
    }

    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerEntries);
    drawerList.setAdapter(adapter);

    drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < contentPages.size()) {
          selectPage(position);
          activeCharacter.setLastOpenPage(position);
        }
        else {
          otherDrawerCommands.get(position - contentPages.size()).getAction().act();
        }
      }
    });
  }

  private void setUpDrawer() {
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
      /** Called when a drawer has settled in a completely open state. */
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
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
    currentPageNumber = pageNumber;
    Fragment displayPage = contentPages.get(pageNumber);
    activeCharacter.setLastOpenPage(pageNumber);
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.content_frame, displayPage)
        .commit();

    drawerLayout.closeDrawer(drawerList);
  }

  private void setTitle() {
    String title = activeCharacter.getName() + " - " + contentPages.get(currentPageNumber).getTitle();
    getSupportActionBar().setTitle(title);
  }

  private void autoSaveCharacter() {
    final Handler handler = new Handler();
    timer = new Timer();
    TimerTask saveCharacter = new TimerTask() {
      @Override
      public void run() {
        handler.post(new Runnable() {
          public void run() {
            Log.i(LOG_TAG, "Autosave: " + activeCharacter.getName());
            CharacterManager.saveActiveCharacter();
          }
        });
      }
    };

    timer.scheduleAtFixedRate(saveCharacter, THIRTY_SECONDS, THIRTY_SECONDS);
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
}
