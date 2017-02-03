package org.raincitygamers.holocron.ui.pages.basics;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;

public class DefenseFragment extends Fragment {
  public DefenseFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Character pc = CharacterManager.getActiveCharacter();

    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_defense, container, false);

    TextView woundsValue = (TextView)view.findViewById(R.id.wounds_value);
    woundsValue.setText(String.format("%d / %d", pc.getWounds(), pc.getWoundThreshold()));

    TextView strainValue = (TextView)view.findViewById(R.id.strain_value);
    strainValue.setText(String.format("%d / %d", pc.getStrain(), pc.getStrainThreshold()));

    TextView soakValue = (TextView)view.findViewById(R.id.soak_value);
    soakValue.setText(String.format("%d", pc.getSoak()));

    TextView defenseValue = (TextView)view.findViewById(R.id.defense_value);
    defenseValue.setText(String.format("%d / %d", pc.getMeleeDefense(), pc.getRangedDefense()));

    return view;
  }
}
