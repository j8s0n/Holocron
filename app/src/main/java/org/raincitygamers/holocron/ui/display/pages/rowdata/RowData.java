package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;

public interface RowData {
  @NotNull
  Type getType();

  enum Type {
    ABILITY,
    ADDER,
    ATTACK_ACTION,
    BUTTON,
    DICE_POOL,
    EDIT_CONDITION,
    INVENTORY,
    KEY_VALUE,
    SECTION_ID,
    SKILL_ACTION,
    THRESHOLD,
    TOGGLE
  }
}
