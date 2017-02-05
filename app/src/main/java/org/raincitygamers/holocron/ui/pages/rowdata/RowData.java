package org.raincitygamers.holocron.ui.pages.rowdata;

import org.jetbrains.annotations.NotNull;

public interface RowData {
  @NotNull
  Type getType();

  public enum Type {
    ABILITY,
    KEY_VALUE,
    SECTION_ID
  }
}
