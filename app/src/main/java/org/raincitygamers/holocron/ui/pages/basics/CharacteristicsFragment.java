package org.raincitygamers.holocron.ui.pages.basics;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;

public class CharacteristicsFragment extends Fragment {
  private final CharacterManager characterManager = CharacterManager.getInstance();

  public CharacteristicsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Character activeCharacter = characterManager.getActiveCharacter();
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_characteristics, container, false);

    TextView agilityScore = (TextView)view.findViewById(R.id.agility_score);
    agilityScore.setText(String.valueOf(activeCharacter.getCharacteristicScore(Characteristic.AGILITY)));

    TextView brawnScore = (TextView)view.findViewById(R.id.brawn_score);
    brawnScore.setText(String.valueOf(activeCharacter.getCharacteristicScore(Characteristic.BRAWN)));

    TextView cunningScore = (TextView)view.findViewById(R.id.cunning_score);
    cunningScore.setText(String.valueOf(activeCharacter.getCharacteristicScore(Characteristic.CUNNING)));

    TextView intellectScore = (TextView)view.findViewById(R.id.intellect_score);
    intellectScore.setText(String.valueOf(activeCharacter.getCharacteristicScore(Characteristic.INTELLECT)));

    TextView presenceScore = (TextView)view.findViewById(R.id.presence_score);
    presenceScore.setText(String.valueOf(activeCharacter.getCharacteristicScore(Characteristic.PRESENCE)));

    TextView willpowerScore = (TextView)view.findViewById(R.id.willpower_score);
    willpowerScore.setText(String.valueOf(activeCharacter.getCharacteristicScore(Characteristic.WILLPOWER)));

    return view;
  }
}
