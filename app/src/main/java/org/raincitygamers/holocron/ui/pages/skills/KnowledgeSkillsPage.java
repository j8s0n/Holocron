package org.raincitygamers.holocron.ui.pages.skills;

import org.raincitygamers.holocron.rules.abilities.Skill;
import org.raincitygamers.holocron.rules.character.SkillManager;

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
    return SkillManager.getInstance().getKnowledgeSkills();
  }
}
