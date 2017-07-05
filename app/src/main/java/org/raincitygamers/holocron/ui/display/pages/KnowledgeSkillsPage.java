package org.raincitygamers.holocron.ui.display.pages;

import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.rules.managers.SkillManager;

import java.util.Collection;

public class KnowledgeSkillsPage extends SkillsPage {
  public KnowledgeSkillsPage() {
  }

  @Override
  public String getTitle() {
    return "Knowledge Skills";
  }

  @Override
  protected Collection<Skill> getSkills() {
    return SkillManager.getKnowledgeSkills();
  }
}
