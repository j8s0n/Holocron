package org.raincitygamers.holocron.rules.traits;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
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
        die.setImageResource(R.drawable.ic_proficiency_die);
        layout.addView(die);
        layoutWidth += die.getWidth();
      }
    }

    if (pool.containsKey(BonusType.ABILITY_DIE)) {
      for (int i = 0; i < pool.get(BonusType.ABILITY_DIE); i++) {
        ImageView die = new ImageView(context);
        die.setImageResource(R.drawable.ic_ability_die);
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

  public enum BonusType {
    ABILITY_DIE,
    ADVANTAGE,
    BOOST_DIE,
    PROFICIENCY_DIE,
    SETBACK_DIE,
    SKILL_RANK,
    SUCCESS,
    THREAT,
    TRIUMPH
  }
}
