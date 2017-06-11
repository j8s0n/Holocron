package com.moosecoders.holocron.ui.display.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.ui.ContentPage;
import com.moosecoders.holocron.ui.FragmentInvalidator;
import com.moosecoders.holocron.ui.display.DisplayArrayAdapter;
import com.moosecoders.holocron.ui.display.rowdata.RowData;

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
