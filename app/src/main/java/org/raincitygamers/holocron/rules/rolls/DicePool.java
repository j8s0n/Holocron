package org.raincitygamers.holocron.rules.rolls;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.abilities.Skill;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Created by jason on 1/9/16.
 */
public class DicePool {
  @Getter
  private final Characteristic characteristic;
  @Getter private final Skill skill;
  private int baseYellowCount;
  private int baseGreenCount;

  private Map<DieType, Integer> modifierDice = new HashMap<>();

  public DicePool(@NotNull Characteristic characteristic, @NotNull Skill skill) {

    this.characteristic = characteristic;
    this.skill = skill;
  }

  public void recalculatePool(int characteristicScore, int skillScore) {
    baseYellowCount = Math.min(characteristicScore, skillScore);
    baseGreenCount = Math.abs(characteristicScore - skillScore);
  }

  public int getModifierDiceCount(@NotNull DieType dieType) {
    int base = 0;
    if (dieType == DieType.GREEN) {
      base = baseGreenCount;
    }
    else if (dieType == DieType.YELLOW) {
      base = baseYellowCount;
    }

    return base + modifierDice.get(dieType);
  }

  public void setModifierDiceCount(@NotNull DieType dieType, int count) {
    modifierDice.put(dieType, count);
  }

  public void addModifierDie(@NotNull DieType dieType) {
    modifierDice.put(dieType, 1 + modifierDice.get(dieType));
  }

  public void removeModifierDie(@NotNull DieType dieType) {
    modifierDice.put(dieType, Math.max(0, modifierDice.get(dieType) - 1));
  }

  /* ????
  public void upgrade() {

  }

  public void downgrade() {

  }
  */
}
