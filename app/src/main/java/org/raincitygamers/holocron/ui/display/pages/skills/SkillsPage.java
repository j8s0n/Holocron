package org.raincitygamers.holocron.ui.display.pages.skills;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.ui.ContentPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SkillsPage extends ContentPage {
  private List<DicePool> skillRatings = new ArrayList<>();
  private SkillArrayAdapter skillArrayAdapter;

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
      DicePool dicePool = DicePool.of(skill.getCharacteristic(), skill);
      skillRatings.add(dicePool);
    }

    skillArrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_skills, container, false);

    ListView skillListView = (ListView) result.findViewById(R.id.skill_group_list);
    skillArrayAdapter = new SkillArrayAdapter(getActivity(), skillRatings);
    skillListView.setAdapter(skillArrayAdapter);

    return result;
  }
}
