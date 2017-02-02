package org.raincitygamers.holocron.ui.display;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.pages.basics.BasicsPage;
import org.raincitygamers.holocron.ui.pages.description.DescriptionPage;
import org.raincitygamers.holocron.ui.pages.skills.CombatSkillsPage;
import org.raincitygamers.holocron.ui.pages.skills.GeneralSkillsPage;
import org.raincitygamers.holocron.ui.pages.skills.KnowledgeSkillsPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CharacterActivity extends AppCompatActivity implements DisplayPage.OnFragmentInteractionListener {
  private ListView drawerList;
  private DrawerLayout drawerLayout;
  private ArrayAdapter<String> adapter;
  private ActionBarDrawerToggle drawerToggle;
  private Character activeCharacter;
  private int currentPageNumber = 0;

  private static final String LOG_TAG = CharacterActivity.class.getSimpleName();
  private static final long THIRTY_SECONDS = 3000;
  private Timer timer;

  private List<DisplayPage> displayPages = Arrays.asList(
      // This is where we populate what shows up in the menu.
      new BasicsPage(), // TODO: defense/wounds/strain.
      // new ActionsTab(),
      new GeneralSkillsPage(),
      new CombatSkillsPage(),
      new KnowledgeSkillsPage(),
      // new TalentsTab(),
      // new EquipmentTab(),
      // new ForceTab(),
      new DescriptionPage()
  );

  @Override
  protected void onPause() {
    super.onPause();

    timer.cancel();
    CharacterManager.getInstance().saveActiveCharacter();
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

    activeCharacter = CharacterManager.getActiveCharacter();
    drawerList = (ListView) findViewById(R.id.navList);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    addDrawerItems();
    setupDrawer();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    selectPage(activeCharacter.getLastOpenPage());
    setTitle();
  }

  private void addDrawerItems() {
    List<String> pageNames = new ArrayList<>();
    for (DisplayPage page : displayPages) {
      pageNames.add(page.getTitle());
    }

    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pageNames);
    drawerList.setAdapter(adapter);

    drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectPage(position);
        activeCharacter.setLastOpenPage(position);
      }
    });
  }

  private void setupDrawer() {
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
    Fragment displayPage = displayPages.get(pageNumber);
    activeCharacter.setLastOpenPage(pageNumber);
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.content_frame, displayPage)
        .commit();

    drawerLayout.closeDrawer(drawerList);
  }

  private void setTitle() {
    String title = activeCharacter.getName() + " - " + displayPages.get(currentPageNumber).getTitle();
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
