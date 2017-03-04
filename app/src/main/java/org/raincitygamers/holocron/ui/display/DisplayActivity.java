package org.raincitygamers.holocron.ui.display;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;
import org.raincitygamers.holocron.ui.display.pages.ActionsPage;
import org.raincitygamers.holocron.ui.display.pages.BasicsPage;
import org.raincitygamers.holocron.ui.display.pages.CombatSkillsPage;
import org.raincitygamers.holocron.ui.display.pages.DescriptionPage;
import org.raincitygamers.holocron.ui.display.pages.ForcePowersPage;
import org.raincitygamers.holocron.ui.display.pages.GearPage;
import org.raincitygamers.holocron.ui.display.pages.GeneralSkillsPage;
import org.raincitygamers.holocron.ui.display.pages.KnowledgeSkillsPage;
import org.raincitygamers.holocron.ui.display.pages.TalentsPage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends ActivityBase {
  private ListView drawerList;
  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;
  private Character activeCharacter;
  private int currentPageNumber = 0;

  private static final String LOG_TAG = DisplayActivity.class.getSimpleName();
  private static final long THIRTY_SECONDS = 300000;
  // private static final long THIRTY_SECONDS = 3000;
  private Timer timer;

  private final List<ContentPage> contentPages = new ArrayList<>();
  private final List<DrawerCommand> otherDrawerCommands = new ArrayList<>();

  public DisplayActivity() {
    activeCharacter = CharacterManager.getActiveCharacter();
    // This is where we populate what shows up in the menu.
    // If it's white, I need to add an empty constructor.
    contentPages.add(new BasicsPage());
    contentPages.add(new ActionsPage());
    contentPages.add(new GeneralSkillsPage());
    contentPages.add(new CombatSkillsPage());
    contentPages.add(new KnowledgeSkillsPage());
    contentPages.add(new GearPage());
    contentPages.add(new TalentsPage());
    if (activeCharacter != null && activeCharacter.getForceRating() > 0) {
      contentPages.add(new ForcePowersPage());
    }

    contentPages.add(new DescriptionPage());
    otherDrawerCommands.add(new DrawerCommand("Edit", new CommandAction() {
      @Override
      public void act() {
        drawerLayout.closeDrawer(drawerList);
        CharacterManager.setActiveCharacter(activeCharacter);
        CharacterManager.saveCharacter(DisplayActivity.this, activeCharacter);
        Intent intent = new Intent(DisplayActivity.this, ChooserActivity.class);
        intent.putExtra(EDIT_ACTIVE_CHARACTER, true);
        intent.putExtra(CURRENT_OPEN_PAGE, contentPages.get(currentPageNumber).getTitle());
        startActivity(intent);
      }
    }));
    otherDrawerCommands.add(new DrawerCommand("Share", new CommandAction() {
      @Override
      public void act() {
        sendCharacter();
      }
    }));
  }

  @Override
  protected void onPause() {
    super.onPause();

    timer.cancel();
    CharacterManager.saveActiveCharacter(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    autoSaveCharacter();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    if (intent.getData() != null) {
      CharacterManager.loadCharacterFromContent(intent.getData(), this, new Runnable() {
        @Override
        public void run() {
          finishingTouches();
        }
      });
    }

    setContentView(R.layout.activity_character);
    sendBroadcast(new Intent(ChooserActivity.ACTION_FINISH));

    drawerList = (ListView) findViewById(R.id.navList);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    addDrawerItems();
    setUpDrawer();

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }

    finishingTouches();
  }

  private void finishingTouches() {
    activeCharacter = CharacterManager.getActiveCharacter();
    if (activeCharacter != null) {
      selectPage(activeCharacter.getLastOpenPage());
      setTitle();
    }
  }

  private void addDrawerItems() {
    List<String> drawerEntries = new ArrayList<>();
    for (ContentPage page : contentPages) {
      drawerEntries.add(page.getTitle());
    }

    for (DrawerCommand command : otherDrawerCommands) {
      drawerEntries.add(command.getLabel());
    }

    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerEntries);
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
          actionBar.setTitle("Switch Page");
        }

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
    drawerLayout.addDrawerListener(drawerToggle);
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
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(title);
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
            Log.i(LOG_TAG, "Auto save: " + activeCharacter.getName());
            CharacterManager.saveActiveCharacter(DisplayActivity.this);
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
  public boolean onOptionsItemSelected(MenuItem item) {
    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  private void sendCharacter() {
    try {
      Character character = CharacterManager.getActiveCharacter();
      File tempFile = new File(getExternalCacheDir(), character.getFileName());
      FileOutputStream fos = new FileOutputStream(tempFile);
      fos.write(character.toJsonObject().toString().getBytes());

      Uri path = Uri.fromFile(tempFile);
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("application/json");
      intent.putExtra(Intent.EXTRA_STREAM, path);
      startActivity(Intent.createChooser(intent, "Send character..."));
    }
    catch (IOException | JSONException e) {
      Log.e(LOG_TAG, "Error sending character as attachment.", e);
    }
  }
}
