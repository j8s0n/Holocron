package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;

public interface RowData {
  @NotNull
  Type getType();

  enum Type {
    ABILITY,
    ATTACK_ACTION,
    BUTTON,
    INVENTORY,
    KEY_VALUE,
    SECTION_ID,
    SKILL_ACTION,
    TOGGLE
  }
}
