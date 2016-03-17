package org.raincitygamers.holocron.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.character.Career;
import org.raincitygamers.holocron.rules.character.CareerManager;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.character.Species;
import org.raincitygamers.holocron.rules.character.SpeciesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NewCharacterActivity extends ActivityBase {
  private static final String LOG_TAG = NewCharacterActivity.class.getSimpleName();
  private final List<String> specializations = new ArrayList<>();
  private Species selectedSpecies;
  private Career selectedCareer;
  private Specialization selectedSpecialization;
  private final CareerManager careerManager = CareerManager.getInstance();
  private final SpeciesManager speciesManager = SpeciesManager.getInstance();
  private final CharacterManager characterManager = CharacterManager.getInstance();
  private final List<String> careers = careerManager.getCareerNames();
  private final List<String> species = speciesManager.getSpeciesNames();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_character);
    buildSpeciesSpinner();
    buildCareerSpinner();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  public void onDoneClicked(View view) {
    Log.i(LOG_TAG, "onDoneClicked");
    Log.i(LOG_TAG, "Selected: " + selectedCareer + " : " + selectedSpecialization);
    String name = readEditText(R.id.character_name);
    if (name.isEmpty()) {
      Toast.makeText(this, "Who are you?", Toast.LENGTH_LONG).show();
      return;
    }

    Character character = new Character.Builder(name, selectedCareer, selectedSpecialization, selectedSpecies, UUID.randomUUID())
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

    characterManager.saveCharacter(character);
    Intent intent = new Intent(this, DisplayActivity.class);
    intent.putExtra(DisplayActivity.NEW_CHARACTER, character.getCharacterId());
    startActivity(intent);
  }

  private String readEditText(int editTextId) {
    EditText editText = (EditText) findViewById(editTextId);
    return editText.getText().toString();
  }

  private void buildSpeciesSpinner() {
    Spinner spinner = (Spinner) findViewById(R.id.species);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                                                           species);
    spinner.setAdapter(arrayAdapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpecies = speciesManager.getSpecies(species.get(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        selectedSpecies = null;
      }
    });
  }

  private void buildCareerSpinner() {
    Spinner spinner = (Spinner) findViewById(R.id.careers);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
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
    final Spinner spinner = (Spinner) findViewById(R.id.specializations);
    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
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
