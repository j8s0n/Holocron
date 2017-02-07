package org.raincitygamers.holocron.ui.display.pages.gear;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import lombok.Getter;

public class InventoryItemRowData implements RowData {
  @Getter private final InventoryItem item;

  public InventoryItemRowData(@NotNull InventoryItem item) {
    this.item = item;
  }

  @NotNull
  @Override
  public Type getType() {
    return Type.INVENTORY;
  }

  @NotNull
  public static InventoryItemRowData of(@NotNull InventoryItem item) {
    return new InventoryItemRowData(item);
  }
}
