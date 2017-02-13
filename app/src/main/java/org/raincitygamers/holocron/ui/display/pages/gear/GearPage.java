package org.raincitygamers.holocron.ui.display.pages.gear;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public class GearPage extends ContentPage {
  private List<RowData> rowData = new ArrayList<>();
  private GearArrayAdapter gearArrayAdapter;

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

    FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
    assert fab != null;
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO: Add an item here.
        // Probably create a new activity and pass the values back.
      }
    });

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
    rowData.add(KeyValueRowData.of("Encumbrance", String.format("%d / %d", pc.getEncumbrance(),
                                                                pc.getEncumbranceThreshold())));
    for (InventoryItem item : pc.getInventory()) {
      rowData.add(InventoryItemRowData.of(item));
    }

    return rowData;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_inventory, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.gear_group_list);
    gearArrayAdapter = new GearArrayAdapter(getActivity(), rowData);
    skillListView.setAdapter(gearArrayAdapter);

    return result;
  }
}
