package org.raincitygamers.holocron.ui.pages.gear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.ui.display.DisplayPage;

import java.util.ArrayList;
import java.util.List;

public class GearPage extends DisplayPage {
  private List<InventoryItem> inventoryItems = new ArrayList<>();
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
  }

  private void displayGear() {
    inventoryItems.clear();
    inventoryItems.addAll(CharacterManager.getActiveCharacter().getInventory());
    gearArrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_skills_page, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.skill_group_list);
    gearArrayAdapter = new GearArrayAdapter(getActivity(), inventoryItems);
    skillListView.setAdapter(gearArrayAdapter);

    return result;
  }
}
