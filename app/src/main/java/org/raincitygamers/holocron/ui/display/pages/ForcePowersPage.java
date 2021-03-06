package org.raincitygamers.holocron.ui.display.pages;

import org.raincitygamers.holocron.rules.traits.ForcePowerUpgrade;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.ForcePowerManager;
import org.raincitygamers.holocron.ui.display.rowdata.AbilityRowData;
import org.raincitygamers.holocron.ui.display.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.SectionRowData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForcePowersPage extends AbilityPage {
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
    rowData.add(0, KeyValueRowData.of("Force Rating", String.format(Locale.US, "%d", pc.getForceRating()), 0));

    for (Map.Entry<String, List<Integer>> entry : pc.getForcePowers().entrySet()) {
      if (!entry.getValue().isEmpty()) {
        String powerName = entry.getKey();
        String pageName = "Force Powers";
        if (!pc.isSectionHidden(pageName, powerName)) {
          rowData.add(SectionRowData.of(powerName, pageName, false));
          List<ForcePowerUpgrade> powerUpgradeList = ForcePowerManager.getList(powerName);
          List<Integer> chosenPowerUpgrades = entry.getValue();
          Collections.sort(chosenPowerUpgrades);
          for (Integer i : chosenPowerUpgrades) {
            rowData.add(AbilityRowData.of(powerUpgradeList.get(i)));
          }
        }
        else {
          rowData.add(SectionRowData.of(powerName, pageName, true));
        }
      }
    }

    return rowData;
  }
}
