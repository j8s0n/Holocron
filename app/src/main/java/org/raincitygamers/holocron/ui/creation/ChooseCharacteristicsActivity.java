package org.raincitygamers.holocron.ui.creation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.display.DisplayCharacterActivity;

public class ChooseCharacteristicsActivity extends CreationActivity {
  private static final CharacterManager characterManager = CharacterManager.getInstance();
  private static final String LOG_TAG = ChooseCharacteristicsActivity.class.getSimpleName();
  private Character activeCharacter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_characteristics);
    activeCharacter = characterManager.getActiveCharacter();
    assert activeCharacter != null;
    Log.i(LOG_TAG, "Character: " + activeCharacter);

    // TODO:
    // Display the values from activeCharacter
  }

  public void onDoneClicked(View view) {
    Log.i(LOG_TAG, "onDoneClicked");
    Intent intent = new Intent(this, DisplayCharacterActivity.class);
    startActivity(intent);
  }
}
