package org.raincitygamers.holocron.ui.display.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.display.DisplayArrayAdapter;
import org.raincitygamers.holocron.ui.display.pages.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.InventoryItemRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

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
                                                                pc.getEncumbranceThreshold())));
    rowData.add(KeyValueRowData.of("Credits", String.format(Locale.US, "%d", pc.getCredits())));
    for (InventoryItem item : pc.getInventory()) {
      rowData.add(InventoryItemRowData.of(item));
    }

    rowData.add(ButtonRowData.of("Add Item"));

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
