package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.traits.DicePool.BonusType;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ConditionalBonusRowData implements RowData {
  @Getter private final String condition;
  @Getter private final Map<BonusType, Integer> bonuses;

  @NotNull
  @Override
  public Type getType() {
    return Type.CONDITIONAL_BONUS;
  }
}
