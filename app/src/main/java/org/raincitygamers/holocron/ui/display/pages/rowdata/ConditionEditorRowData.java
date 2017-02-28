package org.raincitygamers.holocron.ui.display.pages.rowdata;

import android.view.View.OnClickListener;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ConditionEditorRowData implements RowData {
  @Getter @NonNull private final String condition;
  @Getter @NonNull private final OnClickListener onClickListener;

  @NotNull
  @Override
  public Type getType() {
    return Type.CONDITION_EDITOR;
  }
}
