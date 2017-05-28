package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ToggleRowData implements RowData {
  private final String name;
  @Setter private boolean active;

  @NotNull
  @Override
  public Type getType() {
    return Type.TOGGLE;
  }
}
