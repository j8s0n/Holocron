package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class ThresholdRowData extends KeyValueRowData {
  public static final String WOUNDS = "wounds";
  public static final String STRAIN = "strain";

  @Getter private final String threshold;

  private ThresholdRowData(@NotNull String key, @NotNull String value, @NotNull String threshold) {
    super(key, value);
    this.threshold = threshold;
  }

  public static ThresholdRowData of(@NotNull String key, @NotNull String value, @NotNull String threshold) {
    return new ThresholdRowData(key, value, threshold);
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.THRESHOLD;
  }
}
