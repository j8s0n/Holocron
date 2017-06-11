package com.moosecoders.holocron.ui.chooser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.DrawerActivityBase;
import com.moosecoders.holocron.ui.chooser.pages.BasicsChooser;
import com.moosecoders.holocron.ui.chooser.pages.CharacteristicsChooser;
import com.moosecoders.holocron.ui.chooser.pages.CombatSkillsChooser;
import com.moosecoders.holocron.ui.chooser.pages.DescriptionChooser;
import com.moosecoders.holocron.ui.chooser.pages.ForceChooser;
import com.moosecoders.holocron.ui.chooser.pages.GeneralSkillsChooser;
import com.moosecoders.holocron.ui.chooser.pages.KnowledgeSkillsChooser;
import com.moosecoders.holocron.ui.chooser.pages.TalentsChooser;
import com.moosecoders.holocron.ui.display.DisplayActivity;

import lombok.Getter;
import lombok.Setter;

public class ChooserActivity extends DrawerActivityBase {
  public static final String ACTION_FINISH = ChooserActivity.class.getCanonicalName();
  private FinishReceiver finishReceiver;
  @Getter @Setter private boolean chooserDone = false;

  @Getter private Character activeCharacter;
  @Getter private boolean editActiveCharacter;

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
  public void onBackPressed() {
    if (finishReceiver != null) {
      unregisterReceiver(finishReceiver);
    }

    super.onBackPressed();
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

    selectPage(newPage);
  }

  @Override
  protected void onStop() {
    if (chooserDone && finishReceiver != null) {
      unregisterReceiver(finishReceiver);
    }

    super.onStop();
  }

  @Override
  protected void reactToPageSelection(int position) {

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
