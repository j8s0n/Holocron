package com.moosecoders.holocron.ui.display.rowdata;

import android.view.View.OnClickListener;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class ButtonRowData implements RowData {
  @Getter private final String buttonText;
  @Getter private final OnClickListener onClickListener;

  private ButtonRowData(@NotNull String buttonText, @NotNull OnClickListener onClickListener) {
    this.buttonText = buttonText;
    this.onClickListener = onClickListener;
  }

  public static ButtonRowData of(@NotNull String buttonText, @NotNull OnClickListener onClickListener) {
    return new ButtonRowData(buttonText, onClickListener);
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.BUTTON;
  }
}
