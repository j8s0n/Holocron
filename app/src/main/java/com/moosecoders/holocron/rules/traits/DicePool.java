package org.raincitygamers.holocron.rules.traits;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.AttackAction;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.managers.CharacterManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class DicePool {
  @Getter @NonNull private final Characteristic characteristic;
  @Getter @NonNull private final Skill skill;
  private final Map<BonusType, Integer> bonuses = new LinkedHashMap<>();

  public void populateLayout(@NotNull LinearLayout layout, @NotNull Context context) {
    populateLayout(layout, context, getPool(), Collections.EMPTY_SET);
  }

  public static void populateLayout(@NotNull LinearLayout layout, @NotNull Context context,
                                    @NotNull Map<BonusType, Integer> pool, Set<BonusType> extraBonuses) {
    int layoutWidth = 0;
    layout.removeAllViews();
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.PROFICIENCY_DIE);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.ABILITY_DIE);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.FORCE_DIE);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.BOOST_DIE);
    layoutWidth += placeSetbackDiceInLayout(layout, context, pool);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.TRIUMPH);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.SUCCESS);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.FORCE_DIE);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.FORCE_POINT);
    layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.FAILURE);
    layoutWidth += placeAdvantageOrThreatInLayout(layout, context, pool);
    if (extraBonuses.contains(BonusType.CRITICAL)) {
      layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.CRITICAL);
    }
    if (extraBonuses.contains(BonusType.DAMAGE)) {
      layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.DAMAGE);
    }
    if (extraBonuses.contains(BonusType.SKILL_RANK)) {
      layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.SKILL_RANK);
    }
    if (extraBonuses.contains(BonusType.UPGRADE)) {
      layoutWidth += placeBonusTypeInLayout(layout, context, pool, BonusType.UPGRADE);
    }

    ViewGroup.LayoutParams params = layout.getLayoutParams();
    params.width = layoutWidth;
    layout.setLayoutParams(params);
  }

  private static int placeAdvantageOrThreatInLayout(@NotNull LinearLayout layout, @NotNull Context context,
                                                    @NotNull Map<BonusType, Integer> pool) {
    int advantageCount = pool.containsKey(BonusType.ADVANTAGE) ? pool.get(BonusType.ADVANTAGE) : 0;
    int threatCount = pool.containsKey(BonusType.THREAT) ? pool.get(BonusType.THREAT) : 0;
    int count = Math.abs(advantageCount - threatCount);
    BonusType type = advantageCount > threatCount ? BonusType.ADVANTAGE : BonusType.THREAT;
    return placeBonusInLayout(layout, context, type, count);
  }

  private static int placeSetbackDiceInLayout(@NotNull LinearLayout layout, @NotNull Context context,
                                              @NotNull Map<BonusType, Integer> pool) {
    int width = 0;
    int setbackCount = 0;
    if (pool.containsKey(BonusType.SETBACK_DIE)) {
      setbackCount += pool.get(BonusType.SETBACK_DIE);
    }
    if (pool.containsKey(BonusType.NEGATIVE_SETBACK_DIE)) {
      setbackCount -= pool.get(BonusType.NEGATIVE_SETBACK_DIE);
    }

    if (setbackCount > 0) {
      width += placeBonusInLayout(layout, context, BonusType.SETBACK_DIE, setbackCount);
    }
    else if (setbackCount < 0) {
      width += placeBonusInLayout(layout, context, BonusType.NEGATIVE_SETBACK_DIE, Math.abs(setbackCount));
    }

    return width;
  }

  private static int placeBonusTypeInLayout(@NotNull LinearLayout layout, @NotNull Context context,
                                            @NotNull Map<BonusType, Integer> pool, @NotNull BonusType type) {
    int width = 0;
    if (pool.containsKey(type)) {
      int bonusCount = pool.get(type);
      width += placeBonusInLayout(layout, context, type, bonusCount);
    }

    return width;
  }

  private static int placeBonusInLayout(@NotNull LinearLayout layout, @NotNull Context context,
                                        @NotNull BonusType type, int count) {
    int width = 0;
    for (int i = 0; i < count; i++) {
      ImageView die = new ImageView(context);
      die.setImageResource(type.getResourceId());
      layout.addView(die);
      width += die.getWidth();
    }

    return width;
  }

  @NotNull
  private Map<BonusType, Integer> getPool() {
    Map<BonusType, Integer> pool = new LinkedHashMap<>(bonuses);
    Character pc = CharacterManager.getActiveCharacter();
    int skillScore = pc.getSkillScore(skill);
    if (pool.containsKey(BonusType.SKILL_RANK)) {
      skillScore += pool.get(BonusType.SKILL_RANK);
    }

    int charScore = pc.getCharacteristicScore(characteristic);

    int proficiencyDice = Math.min(skillScore, charScore);
    int abilityDice = Math.abs(skillScore - charScore);
    int upgradeCount = pool.containsKey(BonusType.UPGRADE) ? pool.get(BonusType.UPGRADE) : 0;
    for (int i = 0; i < upgradeCount; i++) {
      if (abilityDice == 0) {
        abilityDice = 1;
      }
      else if (abilityDice > 0) {
        abilityDice--;
        proficiencyDice++;
      }
    }

    if (pool.containsKey(BonusType.ABILITY_DIE)) {
      pool.put(BonusType.ABILITY_DIE, pool.get(BonusType.ABILITY_DIE) + abilityDice);
    }
    else {
      pool.put(BonusType.ABILITY_DIE, abilityDice);
    }

    if (pool.containsKey(BonusType.PROFICIENCY_DIE)) {
      pool.put(BonusType.PROFICIENCY_DIE, pool.get(BonusType.PROFICIENCY_DIE) + proficiencyDice);
    }
    else {
      pool.put(BonusType.PROFICIENCY_DIE, proficiencyDice);
    }

    return pool;
  }

  public int getBonus(@NotNull BonusType bonusType) {
    return bonuses.containsKey(bonusType) ? bonuses.get(bonusType) : 0;
  }

  public boolean isCareerSkill() {
    return CharacterManager.getActiveCharacter().isCareerSkill(skill);
  }

  public int getRating() {
    return CharacterManager.getActiveCharacter().getSkillScore(skill);
  }

  public static DicePool of(SkillAction skillAction) {
    return new DicePool(skillAction.getCharacteristic(), skillAction.getSkill());
  }

  public static DicePool of(AttackAction attackAction) {
    return new DicePool(attackAction.getCharacteristic(), attackAction.getSkill());
  }

  public void increasePool(Map<BonusType, Integer> bonusPool) {
    for (Map.Entry<DicePool.BonusType, Integer> entry : bonusPool.entrySet()) {
      if (bonuses.containsKey(entry.getKey())) {
        bonuses.put(entry.getKey(), entry.getValue() + bonuses.get(entry.getKey()));
      }
      else {
        bonuses.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public enum BonusType {
    ABILITY_DIE("Ability Die", true, R.drawable.ic_ability_die),
    ADVANTAGE("Advantage", true, R.drawable.ic_advantage),
    BOOST_DIE("Boost Die", true, R.drawable.ic_boost_die),
    CRITICAL("Critical", false, R.drawable.ic_critical),
    DAMAGE("Damage", false, R.drawable.ic_damage),
    FAILURE("Failure", true, R.drawable.ic_failure),
    FORCE_DIE("Force Die", true, R.drawable.ic_force_die),
    FORCE_POINT("Force Point", true, R.drawable.ic_force_point),
    NEGATIVE_SETBACK_DIE("Negative Setback Die", true, R.drawable.ic_negative_setback_die),
    PROFICIENCY_DIE("Proficiency Die", true, R.drawable.ic_proficiency_die),
    SETBACK_DIE("Setback Die", true, R.drawable.ic_setback_die),
    SKILL_RANK("Skill Rank", true, R.drawable.ic_skill_rank),
    SUCCESS("Success", true, R.drawable.ic_success),
    THREAT("Threat", true, R.drawable.ic_threat),
    TRIUMPH("Triumph", true, R.drawable.ic_triumph),
    UPGRADE("Upgrade", true, R.drawable.ic_upgrade);

    private String name;
    private boolean skillOnly;
    private int resourceId;

    private static final int SHOW_IN_SKILL_ACTION = 1;
    private static final int SHOW_IN_ATTACK_ACTION = 2;

    BonusType(String name, boolean skillOnly, int resourceId) {
      this.name = name;
      this.skillOnly = skillOnly;
      this.resourceId = resourceId;
    }

    @NotNull
    public String getName() {
      return name;
    }

    public int getResourceId() {
      return resourceId;
    }

    public boolean isSkillOnly() {
      return skillOnly;
    }

    @NotNull
    public static BonusType of(@NotNull String name) {
      for (BonusType bonusType : values()) {
        if (bonusType.toString().equals(name)) {
          return bonusType;
        }
      }

      throw new IllegalArgumentException("Invalid bonus type string: " + name);
    }
  }
}
