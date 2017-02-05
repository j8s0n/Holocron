package org.raincitygamers.holocron.ui.pages.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class KeyValueRowData implements RowData {
  private final KvPair pair;

  private KeyValueRowData(@NotNull String key, @NotNull String value) {
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
