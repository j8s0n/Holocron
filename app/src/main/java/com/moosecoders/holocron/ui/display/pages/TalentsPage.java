package org.raincitygamers.holocron.ui.display.pages;

import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.TalentManager;
import org.raincitygamers.holocron.rules.traits.Talent;
import org.raincitygamers.holocron.ui.display.rowdata.AbilityRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.SectionRowData;

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
      String pageName = "Talents";
      if (!pc.isSectionHidden(pageName, specialization.getLongPrettyName())) {
        rowData.add(SectionRowData.of(specialization.getLongPrettyName(), pageName, false));
        List<Talent> talentTree = TalentManager.getList(specialization);
        List<Integer> chosenTalents = entry.getValue();
        Collections.sort(chosenTalents);
        for (Integer i : chosenTalents) {
          rowData.add(AbilityRowData.of(talentTree.get(i)));
        }
      }
      else {
        rowData.add(SectionRowData.of(specialization.getLongPrettyName(), pageName, true));
      }
    }

    return rowData;
  }

  @Override
  public String getTitle() {
    return "Talents";
  }
}
