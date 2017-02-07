package org.raincitygamers.holocron.ui.display.pages.abilities;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.abilities.Ability;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

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
