package org.raincitygamers.holocron.ui.chooser.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Career;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.CareerManager;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;
import org.raincitygamers.holocron.ui.chooser.ChooserBase;
import org.raincitygamers.holocron.ui.chooser.MoreSpecializationsActivity;

import java.util.ArrayList;
import java.util.List;

public class BasicsChooser extends ChooserBase {
  private static final String LOG_TAG = BasicsChooser.class.getSimpleName();

  private final List<String> specializations = new ArrayList<>();
  private Career selectedCareer;
  private Specialization selectedSpecialization;
  private final List<String> careers = CareerManager.getCareerNames();
  private View view;
  private ChooserActivity parentActivity;

  public BasicsChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose Basics";
  }

  @Override protected String getLogTag() {
    return LOG_TAG;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.choose_basics, container, false);
    parentActivity = (ChooserActivity) getActivity();
    Button button = (Button) view.findViewById(R.id.more_specializations_button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MoreSpecializationsActivity.class);
        CharacterManager.setActiveCharacter(getActiveCharacter());
        startActivity(intent);
      }
    });

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    Character ch = getActiveCharacter();
    setEditText(R.id.character_name, ch.getName());
    setEditText(R.id.character_species, ch.getSpecies());
    setEditText(R.id.height, ch.getHeight());
    setEditText(R.id.weight, ch.getWeight());
    setEditText(R.id.skin_tone, ch.getSkinTone());
    setEditText(R.id.hair_color, ch.getHairColor());
    setEditText(R.id.eye_color, ch.getEyeColor());

    if (parentActivity.isEditActiveCharacter()) {
      setEditText(R.id.age, ch.getAge());
      setEditText(R.id.wound_threshold, ch.getWoundThreshold());
      setEditText(R.id.strain_threshold, ch.getStrainThreshold());
      setEditText(R.id.soak, ch.getSoak());
      setEditText(R.id.melee_defense, ch.getMeleeDefense());
      setEditText(R.id.ranged_defense, ch.getRangedDefense());
      setEditText(R.id.xp, ch.getXp());
    }

    buildCareerSpinner(ch);
  }

  private void setEditText(int resourceId, int value) {
    setEditText(resourceId, Integer.toString(value));
  }

  private void setEditText(int resourceId, String value) {
    View view = getView();
    if (view != null) {
      EditText editText = (EditText) view.findViewById(resourceId);
      editText.setText(value);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.i(LOG_TAG, "onPause");

    Character ch = getActiveCharacter();
    ch.setName(readEditText(R.id.character_name));
    ch.setSpecies(readEditText(R.id.character_species));
    ch.setAge(readIntValue(R.id.age));
    ch.setHeight(readEditText(R.id.height));
    ch.setWeight(readEditText(R.id.weight));
    ch.setSkinTone(readEditText(R.id.skin_tone));
    ch.setHairColor(readEditText(R.id.hair_color));
    ch.setEyeColor(readEditText(R.id.eye_color));

    ch.setWoundThreshold(readIntValue(R.id.wound_threshold));
    ch.setStrainThreshold(readIntValue(R.id.strain_threshold));
    ch.setSoak(readIntValue(R.id.soak));
    ch.setMeleeDefense(readIntValue(R.id.melee_defense));
    ch.setRangedDefense(readIntValue(R.id.ranged_defense));

    ch.setCareer(selectedCareer);
    ch.setPrimarySpecialization(selectedSpecialization);
    ch.setXp(readIntValue(R.id.xp));
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
      }
    });
  }

  private void buildSpecializationSpinner(@NotNull final Career career, @Nullable Character character) {
    final Spinner spinner = (Spinner) view.findViewById(R.id.specializations);
    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                                                                 android.R.layout.simple_dropdown_item_1line,
                                                                 specializations);
    List<String> specializationsList = career.getPrettySpecializations();
    careers.remove(getResources().getString(R.string.select_a_career));
    specializations.clear();
    specializations.addAll(specializationsList);
    arrayAdapter.notifyDataSetChanged();

    spinner.setAdapter(arrayAdapter);
    if (character != null) {
      spinner.setSelection(specializations.indexOf(character.getPrimarySpecialization().getPrettyName()));
    }

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpecialization =
            CareerManager.getSpecialization(career.getName() + "--" + specializations.get(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }
}
