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

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.traits.Talent;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.TalentManager;
import org.raincitygamers.holocron.ui.chooser.ChooserBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

public class TalentsChooser extends ChooserBase {
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

    Button addTalent = (Button) getView().findViewById(R.id.add_talent);
    addTalent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SpinnerEntry entry = (SpinnerEntry)availableTalentsSpinner.getSelectedItem();
        talentsTaken.get(chosenSpecialization).add(entry.index);
        buildTalentSpinners();
      }
    });

    Button removeTalent = (Button) getView().findViewById(R.id.remove_talent);
    removeTalent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SpinnerEntry entry = (SpinnerEntry)knownTalentsSpinner.getSelectedItem();
        int index = talentsTaken.get(chosenSpecialization).indexOf(entry.index);
        talentsTaken.get(chosenSpecialization).remove(index);
        buildTalentSpinners();
      }
    });

    buildSpecializationsSpinner();
  }

  private void buildSpecializationsSpinner() {
    Spinner specializationsSpinner = (Spinner) getView().findViewById(R.id.specialization_spinner);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                                                           specializationNames);
    arrayAdapter.notifyDataSetChanged();
    specializationsSpinner.setAdapter(arrayAdapter);

    specializationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSpecialization = specializations.get(position);
        buildTalentSpinners();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  private void buildTalentSpinners() {
    knownTalentsSpinner = (Spinner) getView().findViewById(R.id.known_spinner);
    availableTalentsSpinner = (Spinner) getView().findViewById(R.id.available_spinner);

    List<SpinnerEntry> knownTalents = new ArrayList<>();
    List<SpinnerEntry> availableTalents = new ArrayList<>();
    List<Integer> talentsKnown = talentsTaken.get(chosenSpecialization);
    List<Talent>  talentTree = TalentManager.getList(chosenSpecialization);

    for (int i = 0; i < talentTree.size(); i++) {
      if (talentsKnown.contains(i)) {
        knownTalents.add(new SpinnerEntry(talentTree.get(i).getName(), i));
      }
      else {
        availableTalents.add(new SpinnerEntry(talentTree.get(i).getName(), i));
      }
    }

    ArrayAdapter<SpinnerEntry> knownTalentsAdapter = new ArrayAdapter<SpinnerEntry>(getActivity(),
                                                                           android.R.layout.simple_dropdown_item_1line,
                                                                           knownTalents);
    ArrayAdapter<SpinnerEntry> availableTalentsAdapter = new ArrayAdapter<SpinnerEntry>(getActivity(),
                                                                           android.R.layout.simple_dropdown_item_1line,
                                                                           availableTalents);

    knownTalentsSpinner.setAdapter(knownTalentsAdapter);
    availableTalentsSpinner.setAdapter(availableTalentsAdapter);
    knownTalentsAdapter.notifyDataSetChanged();
    availableTalentsAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.choose_abilities, container, false);

    return result;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  @RequiredArgsConstructor(suppressConstructorProperties = true)
  private static class SpinnerEntry {
    final String display;
    final int index;

    @Override
    public String toString() {
      return display;
    }
  }
}
