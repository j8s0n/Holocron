package com.moosecoders.holocron.ui.display.pages;

import com.moosecoders.holocron.rules.traits.Skill;
import com.moosecoders.holocron.rules.managers.SkillManager;

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
