package org.raincitygamers.holocron.ui.display.pages;

import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.rules.managers.SkillManager;

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
