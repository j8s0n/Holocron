package org.raincitygamers.holocron.ui.display.pages.skills;

import org.raincitygamers.holocron.rules.abilities.Skill;
import org.raincitygamers.holocron.rules.character.SkillManager;

import java.util.Collection;

public class CombatSkillsPage extends SkillsPage {
  public CombatSkillsPage() {
  }

  @Override
  public String getTitle() {
    return "Combat Skills";
  }

  @Override
  protected Collection<Skill> getSkills() {
    return SkillManager.getCombatSkills();
  }
}
