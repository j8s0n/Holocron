package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.traits.DicePool;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class DicePoolRowData implements RowData {
  @Getter private final DicePool dicePool;

  @NotNull
  @Override
  public Type getType() {
    return Type.DICE_POOL;
  }
}
