package org.raincitygamers.holocron.ui.chooser.pages.talents;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.TalentManager;
import org.raincitygamers.holocron.rules.traits.Ability;
import org.raincitygamers.holocron.rules.traits.Talent;
import org.raincitygamers.holocron.ui.chooser.ChooserBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TalentsChooser extends ChooserBase {
  private static final String LOG_TAG = TalentsChooser.class.getSimpleName();

  private List<Specialization> specializations;
  private Map<Specialization, List<Integer>> talentsTaken;
  private List<String> specializationNames;
  private Specialization chosenSpecialization;

  private Spinner knownTalentsSpinner;
  private Spinner availableTalentsSpinner;

  public TalentsChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose Talents";
  }

  @Override
  public void onResume() {
    super.onResume();
    Character pc = getActiveCharacter();
    specializations = pc.getSpecializations();
    talentsTaken = pc.getTalents();

    // Clean out any specializations the user dropped.
    for (Specialization s : talentsTaken.keySet()) {
      if (!specializations.contains(s)) {
        talentsTaken.remove(s);
      }
    }

    // Add any specializations the user added.
    for (Specialization s : specializations) {
      if (!talentsTaken.containsKey(s)) {
        talentsTaken.put(s, new ArrayList<Integer>());
      }
    }

    specializationNames = new ArrayList<>();
    for (Specialization s : specializations) {
      specializationNames.add(s.getName());
    }

    final View view = getView();
    if (view == null) {
      return;
    }

    Button addTalent = (Button) view.findViewById(R.id.add_talent);
    addTalent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (availableTalentsSpinner.getAdapter().getCount() == 0) {
          return;
        }

        SpinnerEntry entry = (SpinnerEntry) availableTalentsSpinner.getSelectedItem();
        talentsTaken.get(chosenSpecialization).add(entry.index);
        buildTalentSpinners(view);
      }
    });

    Button removeTalent = (Button) view.findViewById(R.id.remove_talent);
    removeTalent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (knownTalentsSpinner.getAdapter().getCount() == 0) {
          return;
        }

        SpinnerEntry entry = (SpinnerEntry) knownTalentsSpinner.getSelectedItem();
        int index = talentsTaken.get(chosenSpecialization).indexOf(entry.index);
        talentsTaken.get(chosenSpecialization).remove(index);
        buildTalentSpinners(view);
      }
    });

    buildSpecializationsSpinner(view);
  }

  private void buildSpecializationsSpinner(@NotNull View view) {
    Spinner specializationsSpinner = (Spinner) view.findViewById(R.id.specialization_spinner);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                                                           specializationNames);
    arrayAdapter.notifyDataSetChanged();
    specializationsSpinner.setAdapter(arrayAdapter);

    specializationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSpecialization = specializations.get(position);
        buildTalentSpinners(view);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  private void buildTalentSpinners(@NotNull View view) {
    knownTalentsSpinner = (Spinner) view.findViewById(R.id.known_spinner);
    availableTalentsSpinner = (Spinner) view.findViewById(R.id.available_spinner);

    List<SpinnerEntry> knownTalents = new ArrayList<>();
    List<SpinnerEntry> availableTalents = new ArrayList<>();
    List<Integer> talentsKnown = talentsTaken.get(chosenSpecialization);
    List<Talent> talentTree = TalentManager.getList(chosenSpecialization);

    for (int i = 0; i < talentTree.size(); i++) {
      if (talentsKnown.contains(i)) {
        knownTalents.add(new SpinnerEntry(talentTree.get(i), i));
      }
      else {
        availableTalents.add(new SpinnerEntry(talentTree.get(i), i));
      }
    }

    ArrayAdapter<SpinnerEntry> knownTalentsAdapter =
        new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, knownTalents);
    ArrayAdapter<SpinnerEntry> availableTalentsAdapter =
        new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, availableTalents);

    knownTalentsSpinner.setAdapter(knownTalentsAdapter);
    availableTalentsSpinner.setAdapter(availableTalentsAdapter);
    knownTalentsAdapter.notifyDataSetChanged();
    availableTalentsAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.choose_talents, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    View view = getView();
    if (view != null) {
      final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }

  private static class SpinnerEntry {
    final String display;
    final int index;
    final int row;
    final int column;

    SpinnerEntry(@NotNull Ability ability, int index) {
      this.index = index;
      this.row = ability.getRow();
      this.column = ability.getColumn();
      this.display = String.format(Locale.US, "%s (Row %d, Col %d)", ability.getName(), row, column);
    }

    @Override
    public String toString() {
      return display;
    }
  }
}
