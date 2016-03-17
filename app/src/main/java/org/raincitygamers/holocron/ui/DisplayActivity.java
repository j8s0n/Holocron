package org.raincitygamers.holocron.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DisplayActivity extends AppCompatActivity {
  private static final String LOG_TAG = DisplayActivity.class.getSimpleName();
  private static final CharacterManager characterManager = CharacterManager.getInstance();
  private static final long THIRTY_SECONDS = 3000;
  private Character character;
  private Timer timer;
  public static final String NEW_CHARACTER = "new_character_object";

  @Override
  protected void onPause() {
    super.onPause();

    timer.cancel();
    characterManager.saveCharacter(character);
  }

  @Override
  protected void onResume() {
    super.onResume();
    autoSaveCharacter();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display);

    UUID characterId = (UUID) getIntent().getExtras().get(NEW_CHARACTER);
    character = characterManager.getCharacter(characterId);
    Log.i(LOG_TAG, "Character: " + character);
  }

  private void autoSaveCharacter() {
    final Handler handler = new Handler();
    timer = new Timer();
    TimerTask saveCharacter = new TimerTask() {
      @Override
      public void run() {
        handler.post(new Runnable() {
          public void run() {
            Log.i(LOG_TAG, "Autosave: " + character.getName());
            characterManager.saveCharacter(character);
          }
        });
      }
    };

    timer.scheduleAtFixedRate(saveCharacter, THIRTY_SECONDS, THIRTY_SECONDS);
  }
}
