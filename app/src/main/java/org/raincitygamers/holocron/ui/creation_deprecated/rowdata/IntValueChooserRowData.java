package org.raincitygamers.holocron.ui.creation_deprecated.rowdata;

import lombok.Getter;

public class IntValueChooserRowData implements ChooserRowData {
  @Getter private int value;
  private final int minValue;
  private final int maxValue;

  private IntValueChooserRowData(int value, int minValue, int maxValue) {
    this.value = value;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public static IntValueChooserRowData of(int value, int minValue, int maxValue) {
    return new IntValueChooserRowData(value, minValue, maxValue);
  }

  @Override
  public Type getType() {
    return Type.INT_VALUE;
  }
}
