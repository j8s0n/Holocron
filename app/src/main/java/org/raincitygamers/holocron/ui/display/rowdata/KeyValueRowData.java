package org.raincitygamers.holocron.ui.display.rowdata;

import android.view.View;
import android.view.View.OnLongClickListener;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class KeyValueRowData implements RowData {
  private final KvPair pair;
  private final int imageId;
  private final View.OnLongClickListener longClickListener;

  KeyValueRowData(@NotNull String key, @NotNull String value, int imageId) {
    this(key, value, imageId, new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        return false;
      }
    });
  }

  private KeyValueRowData(@NotNull String key, @NotNull String value, int imageId,
                          @NotNull OnLongClickListener longClickListener) {
    this.pair = KvPair.of(key, value);
    this.imageId = imageId;
    this.longClickListener = longClickListener;
  }

  public static KeyValueRowData of(@NotNull String key, @NotNull String value, int imageId) {
    return new KeyValueRowData(key, value, imageId);
  }

  public static KeyValueRowData of(@NotNull String key, @NotNull String value, int imageId,
                                   @NotNull OnLongClickListener longClickListener) {
    return new KeyValueRowData(key, value, imageId, longClickListener);
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
