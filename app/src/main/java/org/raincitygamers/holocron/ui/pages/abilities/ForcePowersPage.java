package org.raincitygamers.holocron.ui.pages.abilities;

import org.raincitygamers.holocron.rules.abilities.Ability;
import org.raincitygamers.holocron.rules.character.CharacterManager;

import java.util.List;

public class ForcePowersPage extends AbilityPage {
  public ForcePowersPage() {

  }

  @Override
  public String getTitle() {
    return "Force Powers";
  }

  @Override
  public List<Ability> getAbilities() {
    return CharacterManager.getActiveCharacter().getForcePowers();
  }
}
