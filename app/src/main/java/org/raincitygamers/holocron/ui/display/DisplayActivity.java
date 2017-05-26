package org.raincitygamers.holocron.ui.display;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.DrawerActivityBase;
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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends DrawerActivityBase {
  private Character activeCharacter;

  private static final String LOG_TAG = DisplayActivity.class.getSimpleName();
  private static final long THIRTY_SECONDS = 300000;
  // private static final long THIRTY_SECONDS = 3000;
  private Timer timer;

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
        intent.putExtra(CURRENT_OPEN_PAGE, contentPages.get(currentPage).getTitle());
        startActivity(intent);
      }
    }));
    otherDrawerCommands.add(new DrawerCommand("Share", new CommandAction() {
      @Override
      public void act() {
        drawerLayout.closeDrawer(drawerList);
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

    sendBroadcast(new Intent(ChooserActivity.ACTION_FINISH));

    finishingTouches();
  }

  private void finishingTouches() {
    activeCharacter = CharacterManager.getActiveCharacter();
    if (activeCharacter != null) {
      selectPage(activeCharacter.getLastOpenPage());
      setDefaultTitle();
    }
  }

  @Override
  protected void reactToPageSelection(int position) {
    activeCharacter.setLastOpenPage(position);
  }

  @Override
  protected void selectPage(int pageNumber) {
    super.selectPage(pageNumber);
    activeCharacter.setLastOpenPage(pageNumber);
  }

  @Override
  protected void setDefaultTitle() {
    if (currentPage >= 0 && currentPage < contentPages.size()) {
      setTitle(activeCharacter.getName() + " - " + contentPages.get(currentPage).getTitle());
    }
    else {
      Log.e(LOG_TAG, String.format(Locale.getDefault(), "Current page %d out of range.", currentPage));
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

  private void sendCharacter() {
    try {
      Character character = CharacterManager.getActiveCharacter();
      File tempFile = new File(getExternalCacheDir(), character.getFileName());
      FileOutputStream fos = new FileOutputStream(tempFile);
      fos.write(character.toJsonObject().toString(2).getBytes());

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
