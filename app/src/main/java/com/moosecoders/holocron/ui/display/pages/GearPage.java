package com.moosecoders.holocron.ui.display.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.character.InventoryItem;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.ContentPage;
import com.moosecoders.holocron.ui.display.DisplayArrayAdapter;
import com.moosecoders.holocron.ui.display.InventoryEditorActivity;
import com.moosecoders.holocron.ui.display.WealthTrackerActivity;
import com.moosecoders.holocron.ui.display.rowdata.ButtonRowData;
import com.moosecoders.holocron.ui.display.rowdata.InventoryItemRowData;
import com.moosecoders.holocron.ui.display.rowdata.KeyValueRowData;
import com.moosecoders.holocron.ui.display.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GearPage extends ContentPage {
  private List<RowData> rowData = new ArrayList<>();
  private DisplayArrayAdapter gearArrayAdapter;

  public GearPage() {
  }

  @Override
  public String getTitle() {
    return "Inventory";
  }

  @Override
  public void onResume() {
    super.onResume();
    displayGear();
  }

  private void displayGear() {
    rowData.clear();
    rowData.addAll(getRowData());
    gearArrayAdapter.notifyDataSetChanged();
  }

  @NotNull
  private List<RowData> getRowData() {
    Character pc = CharacterManager.getActiveCharacter();
    List<RowData> rowData = new ArrayList<>();
    rowData.add(KeyValueRowData.of("Encumbrance", String.format(Locale.US, "%d / %d", pc.getEncumbrance(),
                                                                pc.getEncumbranceThreshold()), 0));
    rowData.add(KeyValueRowData.of("Credits", String.format(Locale.US, "%d", pc.getTotalWealth()), R.drawable.ic_credit,
                                   new OnLongClickListener() {
                                     @Override
                                     public boolean onLongClick(View v) {
                                       Intent intent = new Intent(getActivity(), WealthTrackerActivity.class);
                                       getActivity().startActivity(intent);
                                       return true;
                                     }
                                   }));

    for (InventoryItem item : pc.getInventory()) {
      rowData.add(InventoryItemRowData.of(item));
    }

    rowData.add(ButtonRowData.of("Add Item", new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), InventoryEditorActivity.class);
        intent.putExtra(InventoryEditorActivity.INVENTORY_ITEM_TO_EDIT, -1);
        getActivity().startActivity(intent);
      }
    }));
    return rowData;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_inventory, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.gear_group_list);
    gearArrayAdapter = new DisplayArrayAdapter(getActivity(), rowData, null);
    skillListView.setAdapter(gearArrayAdapter);

    return result;
  }
}
