package org.raincitygamers.holocron.ui.display.pages.skills;

import org.raincitygamers.holocron.rules.abilities.Skill;
import org.raincitygamers.holocron.rules.character.SkillManager;

import java.util.Collection;

public class GeneralSkillsPage extends SkillsPage {
  public GeneralSkillsPage() {
  }

  @Override
  public String getTitle() {
    return "General Skills";
  }

  @Override
  protected Collection<Skill> getSkills() {
    return SkillManager.getGeneralSkills();
  }
}
