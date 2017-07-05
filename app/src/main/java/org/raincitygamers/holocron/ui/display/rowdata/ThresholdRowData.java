package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class ThresholdRowData extends KeyValueRowData {
  public static final String WOUNDS = "wounds";
  public static final String STRAIN = "strain";

  @Getter private final String threshold;

  private ThresholdRowData(@NotNull String key, @NotNull String value, @NotNull String threshold, int imageId) {
    super(key, value, imageId);
    this.threshold = threshold;
  }

  public static ThresholdRowData of(@NotNull String key, @NotNull String value, @NotNull String threshold, int imageId) {
    return new ThresholdRowData(key, value, threshold, imageId);
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.THRESHOLD;
  }
}
