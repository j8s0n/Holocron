package org.raincitygamers.holocron.ui.pages.skills;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.abilities.Skill;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.rules.character.SkillManager;
import org.raincitygamers.holocron.rules.character.SkillRating;
import org.raincitygamers.holocron.ui.display.DisplayPage;

import java.util.ArrayList;
import java.util.List;

public class SkillsPage extends DisplayPage {
  private List<SkillRating> skillRatings = new ArrayList<>();
  private SkillArrayAdapter skillArrayAdapter;
  // private ListView skillListView;

  public SkillsPage() {
    // Required empty public constructor
  }

  @Override
  public String getTitle() {
    return "Skills";
  }

  @Override
  public void onResume() {
    super.onResume();
    displaySkills();
  }

  private void displaySkills() {
    SkillManager skillManager = SkillManager.getInstance();
    Character character = CharacterManager.getActiveCharacter();
    skillRatings.clear();

    for (Skill skill : skillManager.getGeneralSkills()) {
      int charScore = character.getCharacteristicScore(skill.getCharacteristic());
      int skillScore = character.getSkillScore(skill);
      int abilityDiceCount = Math.abs(charScore - skillScore);
      int proficiencyDiceCount = Math.min(charScore, skillScore);
      skillRatings.add(new SkillRating(skill, abilityDiceCount, proficiencyDiceCount));
    }

    skillArrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_skills, container, false);

    // skillListView = (ListView) getActivity().findViewById(R.id.skill_list);
    skillArrayAdapter = new SkillArrayAdapter(getActivity(), skillRatings);

    return result;
  }
}
