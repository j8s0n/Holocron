package org.raincitygamers.holocron.ui.pages.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class KeyValueRowData implements RowData {
  private final kvPair pair;

  private KeyValueRowData(@NotNull String key, @NotNull String value) {
    this.pair = kvPair.of(key, value);
  }

  public static KeyValueRowData of(@NotNull String key, @NotNull String value) {
    return new KeyValueRowData(key, value);
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.KEY_VALUE;
  }

  @Getter
  @RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
  public static class kvPair {
    private final String key;
    private final String value;
  }
}
