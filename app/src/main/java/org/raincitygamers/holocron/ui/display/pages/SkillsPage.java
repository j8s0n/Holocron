package org.raincitygamers.holocron.ui.display.pages;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.display.DisplayArrayAdapter;
import org.raincitygamers.holocron.ui.display.pages.rowdata.DicePoolRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SkillsPage extends ContentPage {
  private List<RowData> skillRatings = new ArrayList<>();
  private DisplayArrayAdapter skillArrayAdapter;

  public SkillsPage() {
    // Required empty public constructor
  }

  protected abstract Collection<Skill> getSkills();

  @Override
  public void onResume() {
    super.onResume();
    displaySkills();
  }

  private void displaySkills() {
    skillRatings.clear();
    for (Skill skill : getSkills()) {
      skillRatings.add(DicePoolRowData.of(DicePool.of(skill.getCharacteristic(), skill)));
    }

    skillArrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_skills, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.skill_group_list);
    skillArrayAdapter = new DisplayArrayAdapter(getActivity(), skillRatings, null);
    skillListView.setAdapter(skillArrayAdapter);

    return result;
  }
}
