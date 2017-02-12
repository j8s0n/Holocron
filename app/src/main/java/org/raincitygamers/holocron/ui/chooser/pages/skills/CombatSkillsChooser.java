package org.raincitygamers.holocron.ui.chooser.pages.skills;

import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.ui.chooser.pages.scoregroup.ScoreGroupChooser;
import org.raincitygamers.holocron.ui.chooser.pages.scoregroup.ScoreRating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CombatSkillsChooser extends ScoreGroupChooser {
  public CombatSkillsChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose Combat Skills";
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
    for (Skill skill : SkillManager.getCombatSkills()) {
      scores.add(ScoreRating.of(skill.getName(), pc.getSkillScore(skill), 0, 5));
    }

    return scores;
  }
}
