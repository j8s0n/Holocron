package org.raincitygamers.holocron.ui.creation;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ChooseBasicsActivity extends CreationActivity {
  private static final String LOG_TAG = ChooseBasicsActivity.class.getSimpleName();
  private final List<String> specializations = new ArrayList<>();
  private Career selectedCareer;
  private Specialization selectedSpecialization;
  private final CareerManager careerManager = CareerManager.getInstance();
  private final CharacterManager characterManager = CharacterManager.getInstance();
  private final List<String> careers = careerManager.getCareerNames();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_basics);
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
    String species = readEditText(R.id.character_species);
    if (name.isEmpty()) {
      Toast.makeText(this, "Who are you?", Toast.LENGTH_LONG).show();
      return;
    }
    else if (species.isEmpty()) {
      Toast.makeText(this, "What are you?", Toast.LENGTH_LONG).show();
      return;
    }


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

    characterManager.saveCharacter(character);
    String key = Character.buildFileName(name, characterId);
    characterManager.loadActiveCharacter(key, characterId);
    Intent intent = new Intent(this, ChooseCharacteristicsActivity.class);
    startActivity(intent);
  }

  private String readEditText(int editTextId) {
    EditText editText = (EditText) findViewById(editTextId);
    return editText.getText().toString();
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
