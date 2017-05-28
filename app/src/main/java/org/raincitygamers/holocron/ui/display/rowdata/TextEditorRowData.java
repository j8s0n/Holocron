package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class TextEditorRowData implements RowData {
  @Getter @Setter @NonNull private String textValue;
  @Getter @NonNull private final String hint;
  @Getter @NonNull private final EditTextWatcher watcher;

  @NotNull
  @Override
  public Type getType() {
    return Type.TEXT_EDITOR;
  }

  public interface EditTextWatcher {
    void valueUpdated(@NotNull String value);
  }
}
