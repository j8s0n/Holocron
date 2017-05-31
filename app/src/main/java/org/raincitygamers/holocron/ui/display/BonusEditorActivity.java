package org.raincitygamers.holocron.ui.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.traits.DicePool.BonusType;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.ScoreRowData;
import org.raincitygamers.holocron.ui.display.rowdata.SpinnerRowData;

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

  private DisplayArrayAdapter arrayAdapter;
  private List<RowData> rowData = new ArrayList<>();

  private final Character pc = CharacterManager.getActiveCharacter();

  private String skillActionName;
  private String conditionName;
  private String originalConditionName;
  private List<String> conditions;
  private int index;
  private Map<String, Integer> bonuses = new HashMap<>();

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
    skillActionName = getIntent().getStringExtra(SkillActionEditorActivity.SKILL_ACTION_TO_EDIT);
    conditionName = getIntent().getStringExtra(CONDITION_NAME);
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
    if (conditionName == null || !conditionName.equals("Always Active")) {
      conditions.add(0, "Always Active");
    }

    if (conditionName != null) {
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
      if (bonusType.showInSkillAction()) {
        int count = 0;
        if (bonuses.containsKey(bonusType.toString())) {
          count = bonuses.get(bonusType.toString());
        }
        rowData.add(ScoreRowData.of(bonusType, count, watcher));
      }
    }

    rowData.add(ButtonRowData.of("Done", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent resultIntent = new Intent();
        ArrayList<String> results = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bonuses.entrySet()) {
          int count = entry.getValue();
          if (count > 0) {
            results.add(String.format(Locale.getDefault(), "%s:%d", entry.getKey(), count));
          }
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
    return pc.getName() + " - Skill Action Editor";
  }
}
