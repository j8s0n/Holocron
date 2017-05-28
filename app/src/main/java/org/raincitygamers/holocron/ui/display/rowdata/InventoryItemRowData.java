package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.character.InventoryItem;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class InventoryItemRowData implements RowData {
  @Getter @NonNull private final InventoryItem item;

  @NotNull
  @Override
  public Type getType() {
    return Type.INVENTORY;
  }
}
