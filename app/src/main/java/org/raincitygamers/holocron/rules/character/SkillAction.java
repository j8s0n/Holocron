package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.rules.traits.Skill;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SkillAction {
  private static final String NAME_KEY = "name";
  private static final String CHARACTERISTIC_KEY = "characteristic";
  private static final String SKILL_KEY = "skill";
  private static final String CONDITIONAL_BONUSES_KEY = "conditional_bonuses";
  private static final String CONDITION_KEY = "condition";
  private static final String BONUSES_KEY = "bonuses";
  private static final String TYPE_KEY = "type";
  private static final String COUNT_KEY = "count";

  private final String name;
  private final Characteristic characteristic;
  private final Skill skill;
  private Map<String, Map<DicePool.BonusType, Integer>> conditionalBonusesMap = new HashMap<>();

  @NotNull
  public static SkillAction of(@NotNull JSONObject jsonObject) throws JSONException {
    SkillAction skillAction = new SkillAction(jsonObject.getString(NAME_KEY),
                                              Characteristic.of(jsonObject.getString(CHARACTERISTIC_KEY)),
                                              SkillManager.getSkill(jsonObject.getString(SKILL_KEY)));
    JSONArray conditionalBonuses = jsonObject.getJSONArray(CONDITIONAL_BONUSES_KEY);
    for (int i = 0; i < conditionalBonuses.length(); i++) {
      JSONObject bonusesForCondition = conditionalBonuses.getJSONObject(i);
      String condition = bonusesForCondition.getString(CONDITION_KEY);
      JSONArray bonuses = bonusesForCondition.getJSONArray(BONUSES_KEY);

      Map<DicePool.BonusType, Integer> bonusesMap = new HashMap<>();
      for (int j = 0; j < bonuses.length(); j++) {
        JSONObject bonus = bonuses.getJSONObject(j);
        String type = bonus.getString(TYPE_KEY);
        int count = bonus.getInt(COUNT_KEY);
        bonusesMap.put(DicePool.BonusType.of(type), count);
      }

      skillAction.conditionalBonusesMap.put(condition, bonusesMap);
    }
    return skillAction;
  }

  @NotNull
  public JSONObject toJsonObject() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(CHARACTERISTIC_KEY, characteristic.toString());
    o.put(SKILL_KEY, skill.getName());
    JSONArray conditionalBonuses = new JSONArray();
    for (Map.Entry<String, Map<DicePool.BonusType, Integer>> entry : conditionalBonusesMap.entrySet()) {
      JSONObject bonusesForCondition = new JSONObject();
      bonusesForCondition.put(CONDITION_KEY, entry.getKey());
      JSONArray bonuses = new JSONArray();
      for (Map.Entry<DicePool.BonusType, Integer> bonus : entry.getValue().entrySet()) {
        JSONObject bonusEntry = new JSONObject();
        bonusEntry.put(TYPE_KEY, bonus.getKey().toString());
        bonusEntry.put(COUNT_KEY, bonus.getValue());
        bonuses.put(bonusEntry);
      }

      bonusesForCondition.put(BONUSES_KEY, bonuses);
      conditionalBonuses.put(bonusesForCondition);
    }

    o.put(CONDITIONAL_BONUSES_KEY, conditionalBonuses);
    return o;
  }
}
