package org.raincitygamers.holocron.ui.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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

import static org.raincitygamers.holocron.ui.display.BonusEditorActivity.BONUS_ARRAY;
import static org.raincitygamers.holocron.ui.display.BonusEditorActivity.CONDITION_NAME;

public class SkillActionEditorActivity extends ActivityBase implements FragmentInvalidator {
  public static final String SKILL_ACTION_TO_EDIT = "SKILL_ACTION_TO_EDIT";
  private static final int BONUS_EDITOR_ACTIVITY = 0;
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
      skillActionBuilder.setName(name);
      characteristic = skillActionToEdit.getCharacteristic().toString();
      skill = skillActionToEdit.getSkill().getName();
      Map<String, Map<BonusType, Integer>> bonusesMap = skillActionToEdit.getConditionalBonuses();
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
        Intent intent = new Intent(SkillActionEditorActivity.this, BonusEditorActivity.class);
        intent.putExtra(SKILL_ACTION_TO_EDIT, skillActionBuilder.getName());
        startActivityForResult(intent, BONUS_EDITOR_ACTIVITY);
      }
    }));

    rowData.add(ButtonRowData.of("Done", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (skillActionBuilder.getName() == null || skillActionBuilder.getName().isEmpty()) {
          Toast.makeText(SkillActionEditorActivity.this, "Please enter a name.", Toast.LENGTH_LONG).show();
          return;
        }

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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
    case BONUS_EDITOR_ACTIVITY: {
      if (resultCode == Activity.RESULT_OK) {
        if (data.hasExtra(CONDITION_NAME) && data.hasExtra(BONUS_ARRAY)) {
          // Added a condition.
          String conditionName = data.getStringExtra(CONDITION_NAME);
          List<String> results = data.getStringArrayListExtra(BONUS_ARRAY);
          for (String bonus : results) {
            int colon = bonus.indexOf(':');
            String bonusName = bonus.substring(0, colon);
            int count = Integer.parseInt(bonus.substring(colon + 1));
            BonusType bonusType = BonusType.of(bonusName);
            skillActionBuilder.addConditional(conditionName, bonusType, count);
          }

        }

        invalidate();
      }
      break;
    }
    }
  }

  @Override
  protected String getTitleString() {
    return pc.getName() + " - Skill Action Editor";
  }

  @Override
  public void invalidate() {
    listComponents();
  }
}
