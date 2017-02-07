package org.raincitygamers.holocron.ui.display.pages.abilities;

import org.raincitygamers.holocron.rules.abilities.Ability;
import org.raincitygamers.holocron.rules.character.CharacterManager;

import java.util.List;

public class TalentsPage extends AbilityPage {
  public TalentsPage() {
  }

  @Override
  public List<Ability> getAbilities() {
    return CharacterManager.getActiveCharacter().getTalents();
  }

  @Override
  public String getTitle() {
    return "Talents";
  }
}
