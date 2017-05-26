package org.raincitygamers.holocron.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class DrawerActivityBase extends ActivityBase {
  protected int currentPage = -1;
  protected ListView drawerList;
  protected DrawerLayout drawerLayout;
  protected ActionBarDrawerToggle drawerToggle;

  private static final String LOG_TAG = DrawerActivityBase.class.getSimpleName();

  protected final List<ContentPage> contentPages = new ArrayList<>();
  protected final List<DrawerCommand> otherDrawerCommands = new ArrayList<>();

  public DrawerActivityBase() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.menu_layout);

    drawerList = (ListView) findViewById(R.id.navList);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    addDrawerItems();
    setUpDrawer();

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }

    setTitle(getTitleString());
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
          reactToPageSelection(position);
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
        setTitle("Switch Page");

        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }

      /** Called when a drawer has settled in a completely closed state. */
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        setTitle(getTitleString());
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }
    };

    drawerToggle.setDrawerIndicatorEnabled(true);
    drawerLayout.addDrawerListener(drawerToggle);
  }

  @NotNull
  protected String getTitleString() {
    if (currentPage >= 0 && currentPage < contentPages.size()) {
      return contentPages.get(currentPage).getTitle();
    }

    return "";
  }


  protected void selectPage(int pageNumber) {
    if (pageNumber < 0 || pageNumber >= contentPages.size()) {
      Log.e(LOG_TAG, String.format(Locale.getDefault(), "Invalid page number: %d", currentPage));
      return;
    }

    Fragment displayPage = contentPages.get(pageNumber);
    if (pageNumber != currentPage) {
      FragmentManager fragmentManager = getFragmentManager();
      currentPage = pageNumber;
      fragmentManager.beginTransaction().replace(R.id.content_frame, displayPage).commit();
    }


    drawerLayout.closeDrawer(drawerList);
    setDefaultTitle();
  }

  protected void setDefaultTitle() {
    if (currentPage >= 0 && currentPage < contentPages.size()) {
      setTitle(contentPages.get(currentPage).getTitle());
    }
    else {
      Log.e(LOG_TAG, String.format(Locale.getDefault(), "Current page %d out of range.", currentPage));
    }
  }

  protected void setTitle(@NotNull String title) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(title);
    }
  }

  protected abstract void reactToPageSelection(int position);

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
}
