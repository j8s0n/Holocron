package org.raincitygamers.holocron.ui.chooser.pages.basics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.jetbrains.annotations.Nullable;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Career;
import org.raincitygamers.holocron.rules.character.CareerManager;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.ui.chooser.ChooserBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicsChooser extends ChooserBase {
  private static final String LOG_TAG = BasicsChooser.class.getSimpleName();
  private final List<String> specializations = new ArrayList<>();
  private Career selectedCareer;
  private Specialization selectedSpecialization;
  private final List<String> careers = CareerManager.getCareerNames();
  private View view;

  public BasicsChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose Basics";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.choose_basics, container, false);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    Character ch = getActiveCharacter();
    if (ch != null) {
      // TODO: Set values from this.
    }
    buildCareerSpinner(ch);
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.i(LOG_TAG, "onPause");

    Character ch = getActiveCharacter();
    ch.setName(readEditText(R.id.character_name));
    ch.setSpecies(readEditText(R.id.character_species));
    ch.setAge(readEditText(R.id.age));
    ch.setHeight(readEditText(R.id.height));
    ch.setWeight(readEditText(R.id.weight));
    ch.setSkinTone(readEditText(R.id.skin_tone));
    ch.setHairColor(readEditText(R.id.hair_color));
    ch.setEyeColor(readEditText(R.id.eye_color));

    ch.setCareer(selectedCareer);
    ch.getSpecializations().clear();
    ch.getSpecializations().add(selectedSpecialization);
  }

  private String readEditText(int editTextId) {
    EditText editText = (EditText) view.findViewById(editTextId);
    return editText.getText().toString();
  }

  private void buildCareerSpinner(@Nullable final Character character) {
    Spinner spinner = (Spinner) view.findViewById(R.id.careers);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                                           careers);
    spinner.setAdapter(arrayAdapter);
    if (character != null) {
      spinner.setSelection(careers.indexOf(character.getCareer().getName()));
    }

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        selectedCareer = CareerManager.getCareer(careers.get(position));
        buildSpecializationSpinner(selectedCareer, character);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        buildSpecializationSpinner(null, null);
        selectedCareer = null;
        selectedSpecialization = null;
      }
    });
  }

  private void buildSpecializationSpinner(@Nullable Career career, @Nullable Character character) {
    final Spinner spinner = (Spinner) view.findViewById(R.id.specializations);
    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                                                                 android.R.layout.simple_dropdown_item_1line,
                                                                 specializations);
    List<String> specializationsList = career == null ? Collections.<String>emptyList() : career.getSpecializations();
    careers.remove(getResources().getString(R.string.select_a_career));
    specializations.clear();
    specializations.addAll(specializationsList);
    arrayAdapter.notifyDataSetChanged();

    spinner.setAdapter(arrayAdapter);
    if (character != null) {
      spinner.setSelection(specializations.indexOf(character.getSpecializations().get(0).getName()));
    }

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpecialization = CareerManager.getSpecialization(specializations.get(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }
}
