package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class KeyValueRowData implements RowData {
  private final KvPair pair;

  KeyValueRowData(@NotNull String key, @NotNull String value) {
    this.pair = KvPair.of(key, value);
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
  public static class KvPair {
    private final String key;
    private final String value;
  }
}
