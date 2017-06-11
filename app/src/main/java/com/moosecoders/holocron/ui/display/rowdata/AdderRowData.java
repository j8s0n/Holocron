package com.moosecoders.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class AdderRowData implements RowData {
  @Getter @NonNull private final AddPerformer addPerformer;
  @Getter @Setter private String enteredText;

  @NotNull
  @Override
  public Type getType() {
    return Type.ADDER;
  }

  public interface AddPerformer {
    void performAdd(@NotNull String thingToAdd);
  }
}
