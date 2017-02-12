package org.raincitygamers.holocron.ui.display.pages.abilities;

import org.raincitygamers.holocron.rules.traits.ForcePowerUpgrade;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.ForcePowerManager;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SectionRowData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    Character pc = CharacterManager.getActiveCharacter();
    List<RowData> rowData = new ArrayList<>();
    rowData.add(0, KeyValueRowData.of("Force Rating", String.format("%d", pc.getForceRating())));

    for (Map.Entry<String, List<Integer>> entry : pc.getForcePowers().entrySet()) {
      String powerName = entry.getKey();
      rowData.add(SectionRowData.of(powerName));
      List<ForcePowerUpgrade> powerUpgradeList = ForcePowerManager.getList(powerName);
      List<Integer> chosenPowerUpgrades = entry.getValue();
      Collections.sort(chosenPowerUpgrades);
      for (Integer i : chosenPowerUpgrades) {
        rowData.add(AbilityRowData.of(powerUpgradeList.get(i)));
      }
    }
    return rowData;
  }
}
