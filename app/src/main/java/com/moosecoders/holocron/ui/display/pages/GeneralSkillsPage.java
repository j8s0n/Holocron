package org.raincitygamers.holocron.ui.display.pages;

import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.rules.managers.SkillManager;

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
