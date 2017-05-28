package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class TextEditorRowData implements RowData {
  @Getter private final EditTextWatcher watcher;
  @Getter private final String hint;
  @Getter @Setter private String textValue;

  public static TextEditorRowData of(@NotNull String textValue, @NotNull String hint, @NotNull EditTextWatcher watcher) {
    return new TextEditorRowData(textValue, hint, watcher);
  }

  private TextEditorRowData(@NotNull String textValue, @NotNull String hint, @NotNull EditTextWatcher watcher) {
    this.watcher = watcher;
    this.hint = hint;
    this.textValue = textValue;
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.TEXT_EDITOR;
  }

  public interface EditTextWatcher {
    void valueUpdated(@NotNull String value);
  }
}
