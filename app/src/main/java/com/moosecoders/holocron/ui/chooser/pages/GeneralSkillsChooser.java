package com.moosecoders.holocron.ui.chooser.pages;

import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.managers.SkillManager;
import com.moosecoders.holocron.rules.traits.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GeneralSkillsChooser extends ScoreGroupChooser {
  private static final String LOG_TAG = GeneralSkillsChooser.class.getSimpleName();

  public GeneralSkillsChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose General Skills";
  }

  @Override
  public void onPause() {
    super.onPause();

    Character pc = getActiveCharacter();
    for (Map.Entry<String, Integer> entry : getScores().entrySet()) {
      Skill skill = SkillManager.getSkill(entry.getKey());
      pc.setSkillScore(skill, entry.getValue());
    }
  }

  @Override
  protected Collection<ScoreRating> getScoreRatings() {
    List<ScoreRating> scores = new ArrayList<>();
    Character pc = getActiveCharacter();
    for (Skill skill : SkillManager.getGeneralSkills()) {
      boolean isCareerSkill = pc.isCareerSkill(skill);
      scores.add(ScoreRating.of(skill.getName(), pc.getSkillScore(skill), 0, 5, isCareerSkill));
    }

    return scores;
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }
}
