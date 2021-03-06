package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

public interface RowData {
  @NotNull
  Type getType();

  enum Type {
    ABILITY,
    ADDER,
    ATTACK_ACTION,
    BUTTON,
    CONDITIONAL_BONUS,
    DICE_POOL,
    EDIT_CONDITION,
    INVENTORY,
    KEY_VALUE,
    SCORE,
    SECTION_ID,
    SKILL_ACTION,
    SPINNER,
    TEXT_EDITOR,
    THRESHOLD,
    TOGGLE
  }
}
