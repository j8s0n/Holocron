package com.moosecoders.holocron.ui.display.pages;

import com.moosecoders.holocron.rules.traits.Skill;
import com.moosecoders.holocron.rules.managers.SkillManager;

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
