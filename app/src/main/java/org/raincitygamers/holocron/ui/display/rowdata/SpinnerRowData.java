package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SpinnerRowData implements RowData {
  @Getter @NonNull private final List<String> content;
  @Getter @Setter private int selectedItem;
  @Getter @NonNull private final SpinnerWatcher spinnerWatcher;

  @NotNull
  @Override
  public Type getType() {
    return Type.SPINNER;
  }

  public interface SpinnerWatcher {
    void itemSelected(@NotNull String item);
  }
}
