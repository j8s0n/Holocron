package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ToggleRowData implements RowData {
  @NonNull private final String name;
  @Setter @NonNull private boolean active;

  @NotNull
  @Override
  public Type getType() {
    return Type.TOGGLE;
  }
}
