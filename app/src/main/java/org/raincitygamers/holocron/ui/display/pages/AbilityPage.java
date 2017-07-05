package org.raincitygamers.holocron.ui.display.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.DisplayArrayAdapter;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public abstract class AbilityPage extends ContentPage implements FragmentInvalidator {
  private List<RowData> abilities = new ArrayList<>();
  private DisplayArrayAdapter adapter;

  public AbilityPage() {

  }

  protected abstract List<RowData> getRowData();

  @Override
  public void onResume() {
    super.onResume();
    displayAbilities();
  }

  private void displayAbilities() {
    abilities.clear();
    abilities.addAll(getRowData());
    adapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_abilities, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.ability_group_list);
    adapter = new DisplayArrayAdapter(getActivity(), abilities, this);
    skillListView.setAdapter(adapter);

    return result;
  }

  @Override
  public void invalidate() {
    displayAbilities();
  }
}
