package org.raincitygamers.holocron.ui.pages.basics;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.rules.character.Specialization;

import java.util.Set;

public class DescriptiveStatsFragment extends Fragment {
  public DescriptiveStatsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Character activeCharacter = CharacterManager.getActiveCharacter();

    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_descriptive_stats, container, false);

    TextView nameValue = (TextView)view.findViewById(R.id.name_value);
    nameValue.setText(activeCharacter.getName());

    TextView speciesValue = (TextView)view.findViewById(R.id.species_value);
    speciesValue.setText(activeCharacter.getSpecies().getName());

    TextView careerValue = (TextView)view.findViewById(R.id.career_value);
    careerValue.setText(activeCharacter.getCareer().getName());

    TextView specializationValue = (TextView)view.findViewById(R.id.specialization_value);
    Set<Specialization> specializations = activeCharacter.getSpecializations();
    Specialization specialization = (Specialization)specializations.toArray()[0];
    specializationValue.setText(specialization.getName());

    return view;
  }
}
