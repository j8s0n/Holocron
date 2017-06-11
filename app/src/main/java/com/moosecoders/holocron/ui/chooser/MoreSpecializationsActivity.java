package com.moosecoders.holocron.ui.chooser;

import android.os.Bundle;
import android.widget.ListView;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.character.Specialization;
import com.moosecoders.holocron.rules.managers.CareerManager;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.ActivityBase;
import com.moosecoders.holocron.ui.chooser.SpecializationsArrayAdapter.SpecializationRowData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoreSpecializationsActivity extends ActivityBase {
  private List<SpecializationRowData> specializations = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choose_more_specializations);

    Set<Specialization> secondarySpecializations = new HashSet<>();
    Character pc = CharacterManager.getActiveCharacter();
    Specialization primarySpecialization = pc.getPrimarySpecialization();
    secondarySpecializations.addAll(pc.getSecondarySpecializations());

    List<Specialization> specializations = Lists.newArrayList(CareerManager.getSpecializations());
    Collections.sort(specializations);

    for (Specialization specialization : specializations) {
      if (!primarySpecialization.equals(specialization)) {
        this.specializations.add(SpecializationRowData.of(specialization,
                                                          secondarySpecializations.contains(specialization)));
      }
    }

    ListView specializationsListView = (ListView) findViewById(R.id.specializations_list);
    SpecializationsArrayAdapter adapter = new SpecializationsArrayAdapter(this, this.specializations);
    adapter.notifyDataSetChanged();
    specializationsListView.setAdapter(adapter);
  }

  @NotNull
  @Override
  protected String getTitleString() {
    return "More Specializations";
  }
}
