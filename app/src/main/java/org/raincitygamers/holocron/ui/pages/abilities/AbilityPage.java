package org.raincitygamers.holocron.ui.pages.abilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.abilities.Ability;
import org.raincitygamers.holocron.ui.display.DisplayPage;

import java.util.ArrayList;
import java.util.List;

public abstract class AbilityPage extends DisplayPage {
  private List<Ability> abilities = new ArrayList<>();
  private AbilityArrayAdapter adapter;

  public AbilityPage() {

  }

  public abstract List<Ability> getAbilities();

  @Override
  public void onResume() {
    super.onResume();
    displayAbilities();
  }

  private void displayAbilities() {
    abilities.clear();
    abilities.addAll(getAbilities());
    adapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_abilities_page, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.ability_group_list);
    adapter = new AbilityArrayAdapter(getActivity(), abilities);
    skillListView.setAdapter(adapter);

    return result;
  }
}