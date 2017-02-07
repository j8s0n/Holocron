package org.raincitygamers.holocron.ui.creation_deprecated.characteristics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.creation_deprecated.ChooserBase;
import org.raincitygamers.holocron.ui.display.DisplayActivity;

public class CharacteristicsChooser extends ChooserBase {
  private static final String LOG_TAG = CharacteristicsChooser.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle("Choose Characteristics");
    setContentView(R.layout.activity_choose_characteristics);
  }

  public void onUpDownClicked(View view) {
    Log.i(LOG_TAG, "onUpDownClicked");
    int viewId = view.getId();
    if (viewId == R.id.brawn_down_button) {
      updateValue((TextView) findViewById(R.id.brawn_entry), -1);
    }
    else if (viewId == R.id.agility_down_button) {
      updateValue((TextView) findViewById(R.id.agility_entry), -1);
    }
    else if (viewId == R.id.intellect_down_button) {
      updateValue((TextView) findViewById(R.id.intellect_entry), -1);
    }
    else if (viewId == R.id.cunning_down_button) {
      updateValue((TextView) findViewById(R.id.cunning_entry), -1);
    }
    else if (viewId == R.id.willpower_down_button) {
      updateValue((TextView) findViewById(R.id.willpower_entry), -1);
    }
    else if (viewId == R.id.presence_down_button) {
      updateValue((TextView) findViewById(R.id.presence_entry), -1);
    }

    else if (viewId == R.id.brawn_up_button) {
      updateValue((TextView) findViewById(R.id.brawn_entry), 1);
    }
    else if (viewId == R.id.agility_up_button) {
      updateValue((TextView) findViewById(R.id.agility_entry), 1);
    }
    else if (viewId == R.id.intellect_up_button) {
      updateValue((TextView) findViewById(R.id.intellect_entry), 1);
    }
    else if (viewId == R.id.cunning_up_button) {
      updateValue((TextView) findViewById(R.id.cunning_entry), 1);
    }
    else if (viewId == R.id.willpower_up_button) {
      updateValue((TextView) findViewById(R.id.willpower_entry), 1);
    }
    else if (viewId == R.id.presence_up_button) {
      updateValue((TextView) findViewById(R.id.presence_entry), 1);
    }
  }

  private void updateValue(TextView textView, int shift) {
    int value = 2;
    CharSequence chars = textView.getText();
    if (chars != null) {
      value = Integer.parseInt(chars.toString());
    }

    value += shift;
    value = value > 6 ? 6 : value;
    value = value < 1 ? 1 : value;
    textView.setText(String.format("%d", value));
  }

  public void onDoneClicked(View view) {
    Log.i(LOG_TAG, "onDoneClicked");
    Character activeCharacter = CharacterManager.getInstance().getActiveCharacter();
    assert activeCharacter != null;
    activeCharacter.setCharacteristicScore(Characteristic.BRAWN, getValue(R.id.brawn_entry));
    activeCharacter.setCharacteristicScore(Characteristic.AGILITY, getValue(R.id.agility_entry));
    activeCharacter.setCharacteristicScore(Characteristic.INTELLECT, getValue(R.id.intellect_entry));
    activeCharacter.setCharacteristicScore(Characteristic.CUNNING, getValue(R.id.cunning_entry));
    activeCharacter.setCharacteristicScore(Characteristic.WILLPOWER, getValue(R.id.willpower_entry));
    activeCharacter.setCharacteristicScore(Characteristic.PRESENCE, getValue(R.id.presence_entry));

    Intent intent = new Intent(this, DisplayActivity.class);
    startActivity(intent);
  }

  private int getValue(int viewId) {
    TextView view = (TextView) findViewById(viewId);
    return Integer.parseInt(view.getText().toString());
  }
}
