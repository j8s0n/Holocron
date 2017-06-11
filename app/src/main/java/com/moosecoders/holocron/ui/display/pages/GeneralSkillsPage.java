package com.moosecoders.holocron.ui.display.pages;

import com.moosecoders.holocron.rules.traits.Skill;
import com.moosecoders.holocron.rules.managers.SkillManager;

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
