package org.raincitygamers.holocron.ui.display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.DicePool.BonusType;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ConditionalBonusRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.SpinnerRowData;
import org.raincitygamers.holocron.ui.display.rowdata.TextEditorRowData;

import java.util.ArrayList;
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
    rowData.clear();
    String name;
    String characteristic;
    String skill;

    if (skillActionToEdit == null) {
      name = "";
      characteristic = Characteristic.BRAWN.toString();
      skill = SkillManager.getAllSkillNames().get(0);
    }
    else {
      name = skillActionToEdit.getName();
      characteristic = skillActionToEdit.getCharacteristic().toString();
      skill = skillActionToEdit.getSkill().getName();
      Map<String, Map<BonusType, Integer>> bonusesMap = skillActionToEdit.getConditionalBonusesMap();
      for (Map.Entry<String, Map<BonusType, Integer>> entry : bonusesMap.entrySet()) {
        String condition = entry.getKey();
        for (Map.Entry<BonusType, Integer> bonusEntry : entry.getValue().entrySet()) {
          skillActionBuilder.addConditional(condition, bonusEntry.getKey(), bonusEntry.getValue());
        }
      }
    }

    rowData.add(TextEditorRowData.of(name, "Name", new TextEditorRowData.EditTextWatcher() {
      @Override
      public void valueUpdated(@NotNull String value) {
        skillActionBuilder.setName(value);
      }
    }));

    List<String> characteristics = Characteristic.getNames();
    int characteristicIndex = characteristics.indexOf(characteristic);
    rowData.add(SpinnerRowData.of(characteristics, characteristicIndex, new SpinnerRowData.SpinnerWatcher() {
      @Override
      public void itemSelected(@NotNull String item) {
        skillActionBuilder.setCharacteristic(Characteristic.of(item));
      }
    }));

    List<String> skills = SkillManager.getAllSkillNames();
    int skillIndex = skills.indexOf(skill);
    rowData.add(SpinnerRowData.of(skills, skillIndex, new SpinnerRowData.SpinnerWatcher() {
      @Override
      public void itemSelected(@NotNull String item) {
        skillActionBuilder.setSkill(SkillManager.getSkill(item));
      }
    }));

    // Add the conditions layout/row data (with long tap to edit).
    // Populate from builder.
    for (Map.Entry<String, Map<BonusType, Integer>> entry : skillActionBuilder.getConditionals().entrySet()) {
      rowData.add(ConditionalBonusRowData.of(entry.getKey(), entry.getValue()));
    }

    rowData.add(ButtonRowData.of("Add Bonus", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO::::::::::::::::::::::::::::::::::::::::::::::::::::
        // Add the activity to edit/add a bonus.
        // Add an "always" entry to the top of the conditions.
        // How do I get the values back from the bonus activity????
      }
    }));

    rowData.add(ButtonRowData.of("Done", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SkillAction skillAction = skillActionBuilder.build();
        pc.addSkillAction(skillAction);
        if (skillActionToEdit != null && !skillActionToEdit.getName().equals(skillAction.getName())) {
          pc.removeSkillAction(skillActionToEdit.getName());
        }

        finish();
      }
    }));

    if (skillActionToEdit != null) {
      rowData.add(ButtonRowData.of("Remove", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          pc.removeSkillAction(skillActionToEdit.getName());
          finish();
        }
      }));
    }

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
