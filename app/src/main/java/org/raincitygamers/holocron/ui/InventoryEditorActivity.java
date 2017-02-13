package org.raincitygamers.holocron.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.rules.managers.CharacterManager;

public class InventoryEditorActivity extends ActivityBase {
  private static final String LOG_TAG = InventoryEditorActivity.class.getSimpleName();

  public static final String INVENTORY_ITEM_TO_EDIT = "inventory_item_to_edit";
  private int inventoryItemToEdit = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.inventory_editor);
    getSupportActionBar().setTitle("Inventory Editor");
    inventoryItemToEdit = getIntent().getIntExtra(INVENTORY_ITEM_TO_EDIT, -1);

    if (inventoryItemToEdit > -1) {
      Button okayButton = (Button) findViewById(R.id.okay_button);
      okayButton.setText("Update");
      // Populate fields with inventory item.
      InventoryItem item = CharacterManager.getActiveCharacter().getInventory().get(inventoryItemToEdit);
      setEntryValues(item);
    }
    else {
      Button deleteButton = (Button) findViewById(R.id.delete_button);
      deleteButton.setVisibility(View.INVISIBLE);
    }
  }

  private void setEntryValues(@NotNull InventoryItem item) {
    setEditTextValue(R.id.item_name_entry, item.getName());
    setEditTextValue(R.id.item_location_entry, item.getLocation());
    setEditTextValue(R.id.item_encumbrance_entry, String.format("%d", item.getEncumbrance()));
    setEditTextValue(R.id.item_quantity_entry, String.format("%d", item.getQuantity()));
    setEditTextValue(R.id.item_description_entry, item.getDescription());
  }

  private void setEditTextValue(int resourceId, @NotNull String value) {
    EditText editText = (EditText) findViewById(resourceId);
    editText.setText(value);
  }

  public void onOkayClicked(View view) {
    if (inventoryItemToEdit > -1) {
      // Update an item.
      InventoryItem item = CharacterManager.getActiveCharacter().getInventory().get(inventoryItemToEdit);
      item.setName(readEditText(R.id.item_name_entry));
      item.setLocation(readEditText(R.id.item_location_entry));
      item.setEncumbrance(readIntValue(R.id.item_encumbrance_entry));
      item.setQuantity(readIntValue(R.id.item_quantity_entry));
      item.setDescription(readEditText(R.id.item_description_entry));
    }
    else {
      // Add new item.
      InventoryItem item = InventoryItem.of(readEditText(R.id.item_name_entry),
                                            readIntValue(R.id.item_quantity_entry),
                                            readEditText(R.id.item_location_entry),
                                            readIntValue(R.id.item_encumbrance_entry),
                                            readEditText(R.id.item_description_entry), false);

      CharacterManager.getActiveCharacter().getInventory().add(item);
    }

    finish();
  }

  private int readIntValue(int resourceId) {
    try {
      return Integer.parseInt(readEditText(resourceId));
    }
    catch (NumberFormatException e) {
      EditText textField = (EditText) findViewById(resourceId);
      Log.e(LOG_TAG, "Invalid value for " + textField.getHint());
    }

    return 0;
  }

  @NotNull
  private String readEditText(int editTextId) {
    EditText editText = (EditText) findViewById(editTextId);
    return editText.getText().toString();
  }

  public void onDeleteClicked(View view) {
    if (inventoryItemToEdit >= 0) {
      CharacterManager.getActiveCharacter().getInventory().remove(inventoryItemToEdit);
    }

    finish();
  }
}
