package org.raincitygamers.holocron.ui.chooser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.chooser.pages.BasicsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.CharacteristicsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.DescriptionChooser;
import org.raincitygamers.holocron.ui.chooser.pages.ForceChooser;
import org.raincitygamers.holocron.ui.chooser.pages.CombatSkillsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.GeneralSkillsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.KnowledgeSkillsChooser;
import org.raincitygamers.holocron.ui.chooser.pages.TalentsChooser;
import org.raincitygamers.holocron.ui.display.DisplayActivity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ChooserActivity extends ActivityBase {
  public static final String ACTION_FINISH = ChooserActivity.class.getCanonicalName();
  private ListView drawerList;
  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle drawerToggle;
  @Getter private Character activeCharacter;
  private int currentPage = -1;
  private FinishReceiver finishReceiver;
  @Getter @Setter private boolean chooserDone = false;
  @Getter private boolean editActiveCharacter;

  private final List<ContentPage> contentPages = new ArrayList<>();
  private final List<DrawerCommand> otherDrawerCommands = new ArrayList<>();

  public ChooserActivity() {
    contentPages.add(new BasicsChooser());
    contentPages.add(new CharacteristicsChooser());
    contentPages.add(new GeneralSkillsChooser());
    contentPages.add(new CombatSkillsChooser());
    contentPages.add(new KnowledgeSkillsChooser());
    contentPages.add(new TalentsChooser());
    contentPages.add(new ForceChooser());
    contentPages.add(new DescriptionChooser());

    otherDrawerCommands.add(new DrawerCommand("Done", new CommandAction() {
      @Override
      public void act() {
        CharacterManager.setActiveCharacter(activeCharacter);
        CharacterManager.saveCharacter(ChooserActivity.this, activeCharacter);
        if (editActiveCharacter) {
          finish();
        }
        else {
          setChooserDone(true);
          Intent intent = new Intent(ChooserActivity.this, DisplayActivity.class);
          startActivity(intent);
        }
      }
    }));
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    editActiveCharacter = getIntent().getBooleanExtra(EDIT_ACTIVE_CHARACTER, false);
    int newPage = 0;
    if (editActiveCharacter) {
      activeCharacter = CharacterManager.getActiveCharacter();
      String currentOpenPage = getIntent().getStringExtra(CURRENT_OPEN_PAGE);
      // Set the current page to the one matching the page open in the display, or the basics if no matcher.
      for (int i = 0; i < contentPages.size(); i++) {
        if (contentPages.get(i).getTitle().contains(currentOpenPage)) {
          newPage = i;
          break;
        }
      }
    }
    else {
      activeCharacter = Character.of();
      finishReceiver = new FinishReceiver();
      registerReceiver(finishReceiver, new IntentFilter(ACTION_FINISH));
    }

    setContentView(R.layout.activity_character);

    drawerList = (ListView) findViewById(R.id.navList);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    addDrawerItems();
    setUpDrawer();

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }

    selectPage(newPage);
    setTitle();
  }

  @Override
  protected void onStop() {
    if (chooserDone && finishReceiver != null) {
      unregisterReceiver(finishReceiver);
    }

    super.onStop();
  }

  @Override
  public void onBackPressed() {
    if (finishReceiver != null) {
      unregisterReceiver(finishReceiver);
    }

    super.onBackPressed();
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
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
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
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(title);
    }
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

  private final class FinishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_FINISH)) {
        finish();
      }
    }
  }
}
