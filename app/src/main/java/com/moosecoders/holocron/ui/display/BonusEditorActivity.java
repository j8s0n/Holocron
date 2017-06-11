package com.moosecoders.holocron.ui.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.character.SkillAction;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.rules.traits.DicePool.BonusType;
import com.moosecoders.holocron.ui.ActivityBase;
import com.moosecoders.holocron.ui.FragmentInvalidator;
import com.moosecoders.holocron.ui.display.SkillActionEditorActivity.ActionType;
import com.moosecoders.holocron.ui.display.rowdata.ButtonRowData;
import com.moosecoders.holocron.ui.display.rowdata.RowData;
import com.moosecoders.holocron.ui.display.rowdata.ScoreRowData;
import com.moosecoders.holocron.ui.display.rowdata.SpinnerRowData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BonusEditorActivity extends ActivityBase implements FragmentInvalidator {
  private static final String LOG_TAG = BonusEditorActivity.class.getSimpleName();
  public static final String CONDITION_NAME = "CONDITION_NAME";
  public static final String REMOVE_CONDITION = "REMOVE_CONDITION";
  public static final String BONUS_ARRAY = "BONUS_ARRAY";
  public static final String ALWAYS_ACTIVE = "Always Active";

  private DisplayArrayAdapter arrayAdapter;
  private List<RowData> rowData = new ArrayList<>();

  private final Character pc = CharacterManager.getActiveCharacter();

  private String skillActionName;
  private String conditionName;
  private String originalConditionName;
  private List<String> conditions;
  private int index;
  private Map<String, Integer> bonuses = new HashMap<>();
  private ActionType actionType;

  private final ScoreRowData.ScoreRowWatcher watcher = new ScoreRowData.ScoreRowWatcher() {
    @Override
    public void valueUpdated(@NotNull BonusType bonusType, int value) {
      bonuses.put(bonusType.toString(), value);
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_bonus);
    // If either is blank, this is new.
    skillActionName = getIntent().getStringExtra(SkillActionEditorActivity.ACTION_TO_EDIT);
    conditionName = getIntent().getStringExtra(CONDITION_NAME);
    actionType = (ActionType) getIntent().getSerializableExtra(SkillActionEditorActivity.ACTION_TYPE);
    originalConditionName = conditionName;
    if (skillActionName != null && conditionName != null) {
      SkillAction skillAction = pc.getSkillAction(skillActionName);
      bonuses.clear();
      Map<String, Map<BonusType, Integer>> conditionalBonuses = skillAction.getConditionalBonuses();
      if (conditionalBonuses != null) {
        Map<BonusType, Integer> bonusMap = conditionalBonuses.get(conditionName);
        if (bonusMap != null) {
          for (Map.Entry<BonusType, Integer> entry : bonusMap.entrySet()) {
            bonuses.put(entry.getKey().toString(), entry.getValue());
          }
        }
      }
    }

    conditions = pc.getAvailableConditions(skillActionName);
    index = 0;
    conditions.add(0, ALWAYS_ACTIVE);

    if (conditionName != null && !conditionName.equals(ALWAYS_ACTIVE)) {
      conditions.add(1, conditionName);
      index = 1;
    }

    arrayAdapter = new DisplayArrayAdapter(this, rowData, this);
    ListView listView = (ListView) findViewById(R.id.bonus_component_list);
    listView.setAdapter(arrayAdapter);
    populateRows();
  }

  private void populateRows() {
    rowData.add(SpinnerRowData.of(conditions, index, new SpinnerRowData.SpinnerWatcher() {
      @Override
      public void itemSelected(@NotNull String item) {
        conditionName = item;
      }
    }));

    for (BonusType bonusType : BonusType.values()) {
      if (bonusType.isSkillOnly()) {
        int count = 0;
        if (bonuses.containsKey(bonusType.toString())) {
          count = bonuses.get(bonusType.toString());
        }

        rowData.add(ScoreRowData.of(bonusType, count, watcher));
      }
    }

    if (actionType.equals(ActionType.ATTACK)) {
      for (BonusType bonusType : BonusType.values()) {
        if (!bonusType.isSkillOnly()) {
          int count = 0;
          if (bonuses.containsKey(bonusType.toString())) {
            count = bonuses.get(bonusType.toString());
          }

          rowData.add(ScoreRowData.of(bonusType, count, watcher));
        }
      }
    }

    rowData.add(ButtonRowData.of("Done", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent resultIntent = new Intent();
        ArrayList<String> results = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bonuses.entrySet()) {
          int count = entry.getValue();
          results.add(String.format(Locale.getDefault(), "%s:%d", entry.getKey(), count));
        }

        if (originalConditionName != null && !originalConditionName.equals(conditionName)) {
          resultIntent.putExtra(REMOVE_CONDITION, originalConditionName);
        }

        resultIntent.putExtra(CONDITION_NAME, conditionName);
        resultIntent.putExtra(BONUS_ARRAY, results);
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
      }
    }));

    if (skillActionName != null && conditionName != null) {
      rowData.add(ButtonRowData.of("Remove", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent resultIntent = new Intent();
          resultIntent.putExtra(REMOVE_CONDITION, conditionName);
          setResult(Activity.RESULT_OK, resultIntent);
          finish();
        }
      }));
    }
  }

  @Override
  public void invalidate() {
  }

  @Override
  protected String getTitleString() {
    return pc.getName() + " - Bonus Editor";
  }
}
