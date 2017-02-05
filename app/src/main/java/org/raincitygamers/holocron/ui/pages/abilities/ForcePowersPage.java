package org.raincitygamers.holocron.ui.pages.abilities;

import org.raincitygamers.holocron.rules.abilities.Ability;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.pages.rowdata.RowData;

import java.util.List;

public class ForcePowersPage extends AbilityPage {
  private Character pc = CharacterManager.getActiveCharacter();
  public ForcePowersPage() {

  }

  @Override
  public String getTitle() {
    return "Force Powers";
  }

  @Override
  protected List<RowData> getRowData() {
    List<RowData> forcePowers = super.getRowData();
    forcePowers.add(0, KeyValueRowData.of("Force Rating", String.format("%d", pc.getForceRating())));
    return forcePowers;
  }

  @Override
  public List<Ability> getAbilities() {
    return pc.getForcePowers();
  }
}
