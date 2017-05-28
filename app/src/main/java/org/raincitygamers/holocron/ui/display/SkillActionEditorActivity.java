package org.raincitygamers.holocron.ui.display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.TextEditorRowData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillActionEditorActivity extends ActivityBase implements FragmentInvalidator {
  public static final String SKILL_ACTION_TO_EDIT = "SKILL_ACTION_TO_EDIT";
  private DisplayArrayAdapter arrayAdapter;
  private List<RowData> rowData = new ArrayList<>();
  private SkillAction skillActionToEdit;
  private SkillAction.Builder skillActionBuilder = new SkillAction.Builder();
  private final Character pc = CharacterManager.getActiveCharacter();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_skill_actions);
    String skillActionName = getIntent().getStringExtra(SKILL_ACTION_TO_EDIT);
    if (skillActionName != null) {
      skillActionToEdit = pc.getSkillAction(skillActionName);
    }

    arrayAdapter = new DisplayArrayAdapter(this, rowData, this);
    ListView conditions = (ListView) findViewById(R.id.skill_action_components);
    conditions.setAdapter(arrayAdapter);
    listComponents();
  }

  private void listComponents() {
    if (skillActionToEdit == null) {
      skillActionToEdit = SkillAction.of("", Characteristic.BRAWN, SkillManager.getGeneralSkills().get(0),
                                         new HashMap<String, Map<DicePool.BonusType, Integer>>());
    }

    rowData.clear();
    rowData.add(TextEditorRowData.of(skillActionToEdit.getName(), "Name", new TextEditorRowData.EditTextWatcher() {
      @Override
      public void valueUpdated(@NotNull String value) {
        skillActionBuilder.setName(value);
      }
    }));

    // Everything here touches the builder. Nothing touches the skill action, until the user taps done.
    // Add the char spinner.
    // Add the skill spinner.
    // Add an "always" entry to the top of the conditions.
    // Add the conditions view (with long tap to edit).
    // Add the new bonus button.
    // Add the done button.
    // If the skill action name changed, delete the old one.

    // TODO: Update the builder in the spinner's select methods.
    // TODO: Somewhere is a done button. Build the builder and update the pc.
    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  protected String getTitleString() {
    return pc.getName() + " - Skill Action Editor";
  }

  @Override
  public void invalidate() {

  }
}
