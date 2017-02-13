package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class ButtonRowData implements RowData {
  @Getter private final String buttonText;

  private ButtonRowData(@NotNull String buttonText) {
    this.buttonText = buttonText;
  }

  public static ButtonRowData of(@NotNull String buttonText) {
    return new ButtonRowData(buttonText);
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.BUTTON;
  }
}
