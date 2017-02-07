package org.raincitygamers.holocron.ui.creation_deprecated.rowdata;

public interface ChooserRowData {
  Type getType();

  enum Type {
    BUTTON,
    INT_VALUE
  }
}
