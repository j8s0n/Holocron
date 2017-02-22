package org.raincitygamers.holocron.ui.display.pages;

import org.raincitygamers.holocron.rules.traits.Talent;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.TalentManager;
import org.raincitygamers.holocron.ui.display.pages.rowdata.AbilityRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SectionRowData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TalentsPage extends AbilityPage {
  public TalentsPage() {
  }

  @Override
  protected List<RowData> getRowData() {
    List<RowData> rowData = new ArrayList<>();
    Character pc = CharacterManager.getActiveCharacter();

    for (Map.Entry<Specialization, List<Integer>> entry : pc.getTalents().entrySet()) {
      Specialization specialization = entry.getKey();
      rowData.add(SectionRowData.of(specialization.getName()));
      List<Talent> talentTree = TalentManager.getList(specialization);
      List<Integer> chosenTalents = entry.getValue();
      Collections.sort(chosenTalents);
      for (Integer i : chosenTalents) {
        rowData.add(AbilityRowData.of(talentTree.get(i)));
      }
    }

    return rowData;
  }

  @Override
  public String getTitle() {
    return "Talents";
  }
}
