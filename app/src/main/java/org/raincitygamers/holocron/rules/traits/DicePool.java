package org.raincitygamers.holocron.rules.traits;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.managers.CharacterManager;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class DicePool {
  @Getter @NonNull private final Characteristic characteristic;
  @Getter @NonNull private final Skill skill;
  private final Map<BonusType, Integer> bonuses = new HashMap<>();

  public int getBonus(@NotNull BonusType type) {
    return bonuses.get(type);
  }

  public void setBonus(@NotNull BonusType type, int value) {
    bonuses.put(type, value);
  }

  public void populateLayout(@NotNull LinearLayout layout, @NotNull Context context) {
    int layoutWidth = 0;
    layout.removeAllViews();
    Map<BonusType, Integer> pool = getPool();

    if (pool.containsKey(BonusType.PROFICIENCY_DIE)) {
      for (int i = 0; i < pool.get(BonusType.PROFICIENCY_DIE); i++) {
        ImageView die = new ImageView(context);
        die.setImageResource(BonusType.PROFICIENCY_DIE.getResourceId());
        layout.addView(die);
        layoutWidth += die.getWidth();
      }
    }

    if (pool.containsKey(BonusType.ABILITY_DIE)) {
      for (int i = 0; i < pool.get(BonusType.ABILITY_DIE); i++) {
        ImageView die = new ImageView(context);
        die.setImageResource(BonusType.ABILITY_DIE.getResourceId());
        layout.addView(die);
        layoutWidth += die.getWidth();
      }
    }

    ViewGroup.LayoutParams params = layout.getLayoutParams();
    params.width = layoutWidth;
    layout.setLayoutParams(params);
  }

  @NotNull
  public Map<BonusType, Integer> getPool() {
    Map<BonusType, Integer> pool = new HashMap<>(bonuses);
    Character pc = CharacterManager.getActiveCharacter();
    int skillScore = pc.getSkillScore(skill);
    int charScore = pc.getCharacteristicScore(characteristic);

    int proficiencyDice = Math.min(skillScore, charScore);
    int abilityDice = Math.abs(skillScore - charScore);

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

  public boolean isCareerSkill() {
    return CharacterManager.getActiveCharacter().isCareerSkill(skill);
  }

  public int getRating() {
    return CharacterManager.getActiveCharacter().getSkillScore(skill);
  }

  public static DicePool of(SkillAction skillAction) {
    DicePool dicePool = new DicePool(skillAction.getCharacteristic(), skillAction.getSkill());
    // dicePool.bonuses.putAll(skillAction.getBonuses());
    return dicePool;
  }

  public enum BonusType {
    ABILITY_DIE("Ability Die", R.drawable.ic_ability_die),
    ADVANTAGE("Advantage", 0),
    BOOST_DIE("Boost Die", 0),
    PROFICIENCY_DIE("Proficiency Die", R.drawable.ic_proficiency_die),
    SETBACK_DIE("Setback Die", 0),
    SKILL_RANK("Skill Rank", 0),
    SUCCESS("Success", 0),
    THREAT("Threat", 0),
    TRIUMPH("Triumph", 0),
    UPGRADE("Upgrade", 0);

    private String name;
    private int resourceId;

    BonusType(String name, int resourceId) {
      this.name = name;
      this.resourceId = resourceId;
    }

    @NotNull
    public String getName() {
      return name;
    }

    public int getResourceId() {
      return resourceId;
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
