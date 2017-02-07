package org.raincitygamers.holocron.ui.chooser.pages.done;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;
import org.raincitygamers.holocron.ui.display.DisplayActivity;

public class DoneChooser extends ContentPage {
  public DoneChooser() {
  }

  @Override
  public String getTitle() {
    return "Done";
  }

  @Override
  public void onResume() {
    super.onResume();
    Intent intent = new Intent(getActivity(), DisplayActivity.class);
    startActivity(intent);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result = inflater.inflate(R.layout.choose_basics, container, false);
    CharacterManager characterManager = CharacterManager.getInstance();
    Character character = ((ChooserActivity)getActivity()).getActiveCharacter();
    characterManager.setActiveCharacter(character);
    characterManager.saveCharacter(character);

    return result;
  }
}
