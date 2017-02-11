package org.raincitygamers.holocron.ui.display.pages.abilities;

import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.ArrayList;
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
    List<RowData> forcePowers = new ArrayList<>();
    forcePowers.add(0, KeyValueRowData.of("Force Rating", String.format("%d", pc.getForceRating())));
    return forcePowers;
  }
}
