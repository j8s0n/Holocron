package org.raincitygamers.holocron.ui.display.pages.skills;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.character.SkillRating;
import org.raincitygamers.holocron.ui.ContentPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SkillsPage extends ContentPage {
  private List<SkillRating> skillRatings = new ArrayList<>();
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
    Character character = CharacterManager.getActiveCharacter();
    skillRatings.clear();

    for (Skill skill : getSkills()) {
      int charScore = character.getCharacteristicScore(skill.getCharacteristic());
      int skillScore = character.getSkillScore(skill);
      boolean careerSkill = character.isCareerSkill(skill);
      skillRatings.add(SkillRating.of(skill, charScore, skillScore, careerSkill));
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
