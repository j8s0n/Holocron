package com.moosecoders.holocron.ui.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.AttackAction;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.character.SkillAction;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.rules.managers.SkillManager;
import com.moosecoders.holocron.rules.traits.Characteristic;
import com.moosecoders.holocron.rules.traits.DicePool.BonusType;
import com.moosecoders.holocron.ui.ActivityBase;
import com.moosecoders.holocron.ui.FragmentInvalidator;
import com.moosecoders.holocron.ui.display.rowdata.ButtonRowData;
import com.moosecoders.holocron.ui.display.rowdata.ConditionalBonusRowData;
import com.moosecoders.holocron.ui.display.rowdata.RowData;
import com.moosecoders.holocron.ui.display.rowdata.SpinnerRowData;
import com.moosecoders.holocron.ui.display.rowdata.TextEditorRowData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
import static com.moosecoders.holocron.ui.display.BonusEditorActivity.BONUS_ARRAY;
import static com.moosecoders.holocron.ui.display.BonusEditorActivity.CONDITION_NAME;
import static com.moosecoders.holocron.ui.display.BonusEditorActivity.REMOVE_CONDITION;
import static com.moosecoders.holocron.ui.display.SkillActionEditorActivity.ActionType.ATTACK;
import static com.moosecoders.holocron.ui.display.SkillActionEditorActivity.ActionType.SKILL;

public class SkillActionEditorActivity extends ActivityBase implements FragmentInvalidator {
  private static final String LOG_TAG = SkillActionEditorActivity.class.getSimpleName();
  public static final String ACTION_TYPE = "ACTION_TYPE";
  public static final String ACTION_TO_EDIT = "ACTION_TO_EDIT";
  public static final int BONUS_EDITOR_ACTIVITY = 0;
  private DisplayArrayAdapter arrayAdapter;
  private List<RowData> rowData = new ArrayList<>();
  private SkillAction actionToEdit;
  private ActionType actionType;
  private SkillAction.Builder actionBuilder;
  private boolean readFromActionToEdit = true;

  private final Character pc = CharacterManager.getActiveCharacter();

  public enum ActionType {
    SKILL,
    ATTACK,
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_skill_actions);
    actionType = (ActionType) getIntent().getSerializableExtra(ACTION_TYPE);
    String actionName = getIntent().getStringExtra(ACTION_TO_EDIT);
    if (actionType == null) {
      Log.e(LOG_TAG, "NULL action type.");
      finish();
      return; // This prevents warnings about actionName being null below.
    }

    switch (actionType) {
    case SKILL:
      if (actionName != null) {
        actionToEdit = pc.getSkillAction(actionName);
      }

      actionBuilder = new SkillAction.Builder();
      actionType = SKILL;
      break;

    case ATTACK:
      if (actionName != null) {
        actionToEdit = pc.getAttackAction(actionName);
      }

      actionBuilder = new AttackAction.Builder();
      actionType = ATTACK;
      break;
    }

    arrayAdapter = new DisplayArrayAdapter(this, rowData, this);
    ListView conditions = (ListView) findViewById(R.id.skill_action_components);
    conditions.setAdapter(arrayAdapter);
    addListItems();
  }

  private void addListItems() {
    rowData.clear();
    addStandardWidgets();

    if (actionType.equals(ATTACK)) {
      addAttackFields();
    }

    for (Map.Entry<String, Map<BonusType, Integer>> entry : actionBuilder.getConditionals().entrySet()) {
      rowData.add(ConditionalBonusRowData.of(entry.getKey(), actionBuilder.getName(), entry.getValue(), actionType));
    }

    addButtons();
    arrayAdapter.notifyDataSetChanged();
  }

  private void addStandardWidgets() {
    String name;
    String characteristic;
    String skill;

    if (readFromActionToEdit && actionToEdit != null) {
      if (!(actionToEdit instanceof AttackAction)) {
        readFromActionToEdit = false;
      }

      name = actionToEdit.getName();
      actionBuilder.setName(name);
      characteristic = actionToEdit.getCharacteristic().toString();
      actionBuilder.setCharacteristic(actionToEdit.getCharacteristic());
      skill = actionToEdit.getSkill().getName();
      actionBuilder.setSkill(actionToEdit.getSkill());
      Map<String, Map<BonusType, Integer>> bonusesMap = actionToEdit.getConditionalBonuses();
      for (Map.Entry<String, Map<BonusType, Integer>> entry : bonusesMap.entrySet()) {
        String condition = entry.getKey();
        for (Map.Entry<BonusType, Integer> bonusEntry : entry.getValue().entrySet()) {
          actionBuilder.addConditional(condition, bonusEntry.getKey(), bonusEntry.getValue());
        }
      }
    }
    else {
      name = actionBuilder.getName();
      characteristic = actionBuilder.getCharacteristic().toString();
      skill = actionBuilder.getSkill().getName();
    }

    rowData.add(TextEditorRowData.of(name, "Name", TYPE_TEXT_FLAG_CAP_SENTENCES | TYPE_CLASS_TEXT,
                                     new TextEditorRowData.EditTextWatcher() {
                                       @Override
                                       public void valueUpdated(@NotNull String value) {
                                         actionBuilder.setName(value);
                                       }
                                     }));

    List<String> characteristics = Characteristic.getNames();
    int characteristicIndex = characteristics.indexOf(characteristic);
    rowData.add(SpinnerRowData.of(characteristics, characteristicIndex, new SpinnerRowData.SpinnerWatcher() {
      @Override
      public void itemSelected(@NotNull String item) {
        actionBuilder.setCharacteristic(Characteristic.of(item));
      }
    }));

    List<String> skills = SkillManager.getAllSkillNames();
    int skillIndex = skills.indexOf(skill);
    rowData.add(SpinnerRowData.of(skills, skillIndex, new SpinnerRowData.SpinnerWatcher() {
      @Override
      public void itemSelected(@NotNull String item) {
        actionBuilder.setSkill(SkillManager.getSkill(item));
      }
    }));
  }

  private void addAttackFields() {
    String damage;
    String critical;
    AttackAction.Range range;
    String text;

    List<String> ranges = new ArrayList<>();
    for (AttackAction.Range rangeValue : AttackAction.Range.values()) {
      ranges.add(rangeValue.getName());
    }

    AttackAction attackToEdit = (AttackAction) actionToEdit;
    final AttackAction.Builder attackBuilder = (AttackAction.Builder) actionBuilder;

    if (readFromActionToEdit && actionToEdit != null) {
      readFromActionToEdit = false;
      damage = String.valueOf(attackToEdit.getDamage());
      attackBuilder.setDamage(attackToEdit.getDamage());
      critical = String.valueOf(attackToEdit.getCritical());
      attackBuilder.setCritical(attackToEdit.getCritical());
      range = attackToEdit.getRange();
      attackBuilder.setRange(attackToEdit.getRange());
      text = attackToEdit.getText();
      attackBuilder.setText(attackToEdit.getText());
    }
    else {
      damage = String.valueOf(attackBuilder.getDamage());
      critical = String.valueOf(attackBuilder.getCritical());
      range = attackBuilder.getRange();
      text = attackBuilder.getText();
    }

    rowData.add(TextEditorRowData.of(damage, "Damage", TYPE_CLASS_NUMBER, new TextEditorRowData.EditTextWatcher() {
      @Override
      public void valueUpdated(@NotNull String value) {
        try {
          attackBuilder.setDamage(Integer.parseInt(value));
        }
        catch (NumberFormatException e) {
          attackBuilder.setDamage(0);
        }
      }
    }));

    // TODO: Eventuallly make it a spinner with N/A, 1, 2, ..., max crit.

    rowData.add(TextEditorRowData.of(critical, "Critical", TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED,
                                     new TextEditorRowData.EditTextWatcher() {
                                       @Override
                                       public void valueUpdated(@NotNull String value) {
                                         try {
                                           attackBuilder.setCritical(Integer.parseInt(value));
                                         }
                                         catch (NumberFormatException e) {
                                           attackBuilder.setCritical(-1);
                                         }
                                       }
                                     }));

    int selectedRange = ranges.indexOf(range.getName());
    rowData.add(SpinnerRowData.of(ranges, selectedRange, new SpinnerRowData.SpinnerWatcher() {
      @Override
      public void itemSelected(@NotNull String item) {
        attackBuilder.setRange(AttackAction.Range.of(item));
      }
    }));

    rowData.add(TextEditorRowData.of(text, "Notes", TYPE_TEXT_FLAG_CAP_SENTENCES | TYPE_CLASS_TEXT,
                                     new TextEditorRowData.EditTextWatcher() {
                                       @Override
                                       public void valueUpdated(@NotNull String value) {
                                         attackBuilder.setText(value);
                                       }
                                     }));
  }

  private void addButtons() {
    rowData.add(ButtonRowData.of("Add Bonus", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SkillActionEditorActivity.this, BonusEditorActivity.class);
        intent.putExtra(ACTION_TO_EDIT, actionBuilder.getName());
        intent.putExtra(ACTION_TYPE, actionType);
        startActivityForResult(intent, BONUS_EDITOR_ACTIVITY);
      }
    }));

    rowData.add(ButtonRowData.of("Done", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        runDone();
      }
    }));

    if (actionToEdit != null) {
      rowData.add(ButtonRowData.of("Remove", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          switch (actionType) {
          case SKILL:
            pc.removeSkillAction(actionToEdit.getName());
            break;

          case ATTACK:
            pc.removeAttackAction(actionToEdit.getName());
            break;
          }

          finish();
        }
      }));
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
    case BONUS_EDITOR_ACTIVITY:
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
            actionBuilder.addConditional(conditionName, bonusType, count);
          }
        }

        if (data.hasExtra(REMOVE_CONDITION)) {
          String condition = data.getStringExtra(REMOVE_CONDITION);
          actionBuilder.removeConditional(condition);
        }

        runDone();
      }
      break;
    }
  }

  @Override
  protected String getTitleString() {
    return pc.getName() + " - Skill Action Editor";
  }

  @Override
  public void invalidate() {
    addListItems();
  }

  private void runDone() {
    if (actionBuilder.getName() == null || actionBuilder.getName().isEmpty()) {
      Toast.makeText(SkillActionEditorActivity.this, "Please enter a name.", Toast.LENGTH_LONG).show();
      return;
    }

    switch (actionType) {
    case SKILL:
      SkillAction skillAction = actionBuilder.build();
      pc.addSkillAction(skillAction);
      if (actionToEdit != null && !actionToEdit.getName().equals(skillAction.getName())) {
        pc.removeSkillAction(actionToEdit.getName());
      }
      break;

    case ATTACK:
      AttackAction.Builder attackBuilder = (AttackAction.Builder) actionBuilder;
      AttackAction attackAction = (AttackAction) actionBuilder.build();
      pc.addAttackAction(attackAction);
      if (actionToEdit != null && !actionToEdit.getName().equals(attackAction.getName())) {
        pc.removeAttackAction(actionToEdit.getName());
      }
      break;
    }

    finish();
  }
}
