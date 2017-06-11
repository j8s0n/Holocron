package com.moosecoders.holocron.ui.display.pages;

import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.character.Specialization;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.rules.managers.TalentManager;
import com.moosecoders.holocron.rules.traits.Talent;
import com.moosecoders.holocron.ui.display.rowdata.AbilityRowData;
import com.moosecoders.holocron.ui.display.rowdata.RowData;
import com.moosecoders.holocron.ui.display.rowdata.SectionRowData;

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
