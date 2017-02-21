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
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class AttackAction {
  private static final String NAME_KEY = "name";
  private static final String CHARACTERISTIC_KEY = "characteristic";
  private static final String SKILL_KEY = "skill";
  private static final String CONDITIONAL_BONUSES_KEY = "conditional_bonuses";
  private static final String CONDITION_KEY = "condition";
  private static final String BONUSES_KEY = "bonuses";
  private static final String TYPE_KEY = "type";
  private static final String COUNT_KEY = "count";
  private static final String DAMAGE_KEY = "damage";
  private static final String CRITICAL_KEY = "critical";
  private static final String RANGE_KEY = "range";
  private static final String TEXT_KEY = "text";

  private final String name;
  private final Characteristic characteristic;
  private final Skill skill;
  private final int damage;
  private final int critical;
  private final Range range;
  private final String text;
  private Map<String, Map<DicePool.BonusType, Integer>> conditionalBonusesMap = new HashMap<>();

  @NotNull
  public static AttackAction of(@NotNull JSONObject jsonObject) throws JSONException {
    AttackAction attackAction = new AttackAction(jsonObject.getString(NAME_KEY),
                                                 Characteristic.of(jsonObject.getString(CHARACTERISTIC_KEY)),
                                                 SkillManager.getSkill(jsonObject.getString(SKILL_KEY)),
                                                 jsonObject.getInt(DAMAGE_KEY),
                                                 jsonObject.getInt(CRITICAL_KEY),
                                                 Range.valueOf(jsonObject.getString(RANGE_KEY)),
                                                 jsonObject.getString(TEXT_KEY));
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

      attackAction.conditionalBonusesMap.put(condition, bonusesMap);
    }
    return attackAction;
  }

  @NotNull
  JSONObject toJsonObject() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(CHARACTERISTIC_KEY, characteristic.toString());
    o.put(SKILL_KEY, skill.getName());
    o.put(DAMAGE_KEY, damage);
    o.put(CRITICAL_KEY, critical);
    o.put(RANGE_KEY, range.toString());
    o.put(TEXT_KEY, text);

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

  @NotNull
  public Map<DicePool.BonusType, Integer> getPoolBonus(@NotNull Set<String> activeConditions) {
    Map<DicePool.BonusType, Integer> poolBonus = new HashMap<>();
    for (Map.Entry<String, Map<DicePool.BonusType, Integer>> entry : conditionalBonusesMap.entrySet()) {
      if (activeConditions.contains(entry.getKey())) {
        incrementAll(poolBonus, entry.getValue());
      }
    }

    return poolBonus;
  }

  private void incrementAll(Map<DicePool.BonusType, Integer> pool, Map<DicePool.BonusType, Integer> bonus) {
    for (Map.Entry<DicePool.BonusType, Integer> entry : bonus.entrySet()) {
      if (pool.containsKey(entry.getKey())) {
        pool.put(entry.getKey(), entry.getValue() + pool.get(entry.getKey()));
      }
      else {
        pool.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public enum Range {
    ENGAGED("Engaged"),
    SHORT("Short"),
    MEDIUM("Medium"),
    LONG("Long"),
    EXTREME("Extreme");

    @Getter private final String name;

    Range(String name) {
      this.name = name;
    }
  }
}
