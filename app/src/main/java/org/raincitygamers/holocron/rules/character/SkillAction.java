package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.DicePool.BonusType;
import org.raincitygamers.holocron.rules.traits.Skill;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
  @Getter private Map<String, Map<BonusType, Integer>> conditionalBonuses = new LinkedHashMap<>();

  private SkillAction(@NotNull String name, @NotNull Characteristic characteristic, @NotNull Skill skill,
                      @NotNull Map<String, Map<BonusType, Integer>> conditionalBonuses) {
    this.name = name;
    this.characteristic = characteristic;
    this.skill = skill;
    this.conditionalBonuses.putAll(conditionalBonuses);
  }

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

      Map<BonusType, Integer> bonusesMap = new HashMap<>();
      for (int j = 0; j < bonuses.length(); j++) {
        JSONObject bonus = bonuses.getJSONObject(j);
        String type = bonus.getString(TYPE_KEY);
        int count = bonus.getInt(COUNT_KEY);
        bonusesMap.put(BonusType.of(type), count);
      }

      skillAction.conditionalBonuses.put(condition, bonusesMap);
    }
    return skillAction;
  }

  @NotNull
  public static SkillAction of(@NotNull String name, @NotNull Characteristic characteristic, @NotNull Skill skill,
                               @NotNull Map<String, Map<BonusType, Integer>> conditionalBonuses) {
    return new SkillAction(name, characteristic, skill, conditionalBonuses);
  }

  public void removeCondition(@NotNull String condition) {
    conditionalBonuses.remove(condition);
  }

  @NotNull
  JSONObject toJsonObject() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(CHARACTERISTIC_KEY, characteristic.toString());
    o.put(SKILL_KEY, skill.getName());
    JSONArray conditionalBonuses = new JSONArray();
    for (Map.Entry<String, Map<BonusType, Integer>> entry : this.conditionalBonuses.entrySet()) {
      JSONObject bonusesForCondition = new JSONObject();
      bonusesForCondition.put(CONDITION_KEY, entry.getKey());
      JSONArray bonuses = new JSONArray();
      for (Map.Entry<BonusType, Integer> bonus : entry.getValue().entrySet()) {
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
  public Map<BonusType, Integer> getPoolBonus(@NotNull Set<String> activeConditions) {
    Map<BonusType, Integer> poolBonus = new HashMap<>();
    for (Map.Entry<String, Map<BonusType, Integer>> entry : conditionalBonuses.entrySet()) {
      if (activeConditions.contains(entry.getKey())) {
        incrementAll(poolBonus, entry.getValue());
      }
    }

    return poolBonus;
  }

  private void incrementAll(Map<BonusType, Integer> pool, Map<BonusType, Integer> bonus) {
    for (Map.Entry<BonusType, Integer> entry : bonus.entrySet()) {
      if (pool.containsKey(entry.getKey())) {
        pool.put(entry.getKey(), entry.getValue() + pool.get(entry.getKey()));
      }
      else {
        pool.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public static class Builder {
    @Getter @Setter private String name;
    @Getter @Setter private Characteristic characteristic;
    @Getter @Setter private Skill skill;
    @Getter private Map<String, Map<BonusType, Integer>> conditionals = new LinkedHashMap<>();

    public void addConditional(@NotNull String conditionalName, @NotNull BonusType bonusType, int count) {
      if (!conditionals.containsKey(conditionalName)) {
        conditionals.put(conditionalName, new HashMap<BonusType, Integer>());
      }

      conditionals.get(conditionalName).put(bonusType, count);
    }

    public SkillAction build() {
      return SkillAction.of(name, characteristic, skill, conditionals);
    }

    public void from(@NotNull SkillAction skillAction) {
      name = skillAction.name;
      characteristic = skillAction.characteristic;
      skill = skillAction.skill;
      conditionals.clear();
      conditionals.putAll(skillAction.conditionalBonuses);
    }

    public void removeConditional(String condition) {
      conditionals.remove(condition);
    }
  }
}
