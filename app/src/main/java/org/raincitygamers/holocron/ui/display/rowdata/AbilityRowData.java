package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.traits.Ability;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class AbilityRowData implements RowData {
  @Getter private final Ability ability;

  @NotNull
  @Override
  public Type getType() {
    return Type.ABILITY;
  }
}
