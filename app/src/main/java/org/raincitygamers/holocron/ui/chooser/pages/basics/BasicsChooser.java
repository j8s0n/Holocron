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
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.character.Career;
import org.raincitygamers.holocron.rules.character.CareerManager;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BasicsChooser extends ContentPage {
  private static final String LOG_TAG = BasicsChooser.class.getSimpleName();
  private final List<String> specializations = new ArrayList<>();
  private Career selectedCareer;
  private Specialization selectedSpecialization;
  private final CareerManager careerManager = CareerManager.getInstance();
  private final List<String> careers = careerManager.getCareerNames();
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
    buildCareerSpinner();
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.i(LOG_TAG, "onPause");
    Log.i(LOG_TAG, "Selected: " + selectedCareer + " : " + selectedSpecialization);
    final String name = readEditText(R.id.character_name);
    final String species = readEditText(R.id.character_species);

    UUID characterId = UUID.randomUUID();
    Character character = new Character.Builder(name, selectedCareer, selectedSpecialization, species, characterId)
                              .age(readEditText(R.id.age))
                              .height(readEditText(R.id.height))
                              .weight(readEditText(R.id.weight))
                              .skinTone(readEditText(R.id.skin_tone))
                              .hairColor(readEditText(R.id.hair_color))
                              .eyeColor(readEditText(R.id.eye_color))
                              .characteristic(Characteristic.BRAWN, 2)
                              .characteristic(Characteristic.AGILITY, 2)
                              .characteristic(Characteristic.INTELLECT, 2)
                              .characteristic(Characteristic.CUNNING, 2)
                              .characteristic(Characteristic.WILLPOWER, 2)
                              .characteristic(Characteristic.PRESENCE, 2)
                              .build();

    ChooserActivity chooser = (ChooserActivity)getActivity();
    chooser.setActiveCharacter(character);
  }

  private String readEditText(int editTextId) {
    EditText editText = (EditText) view.findViewById(editTextId);
    return editText.getText().toString();
  }

  private void buildCareerSpinner() {
    Spinner spinner = (Spinner) view.findViewById(R.id.careers);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                                           careers);
    spinner.setAdapter(arrayAdapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        selectedCareer = careerManager.getCareer(careers.get(position));
        buildSpecializationSpinner(selectedCareer);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        buildSpecializationSpinner(null);
        selectedCareer = null;
        selectedSpecialization = null;
      }
    });
  }

  private void buildSpecializationSpinner(@Nullable Career career) {
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
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpecialization = careerManager.getSpecialization(specializations.get(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }
}
