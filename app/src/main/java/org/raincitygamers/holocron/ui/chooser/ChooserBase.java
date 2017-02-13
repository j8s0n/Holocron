package org.raincitygamers.holocron.ui.chooser;

import android.util.Log;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.ui.ContentPage;

public abstract class ChooserBase extends ContentPage {
  @NotNull
  protected Character getActiveCharacter() {
    return ((ChooserActivity)getActivity()).getActiveCharacter();
  }

  protected abstract String getLogTag();

  protected int readIntValue(int resourceId) {
    try {
      return Integer.parseInt(readEditText(resourceId));
    }
    catch (NumberFormatException e) {
      EditText textField = (EditText) getView().findViewById(resourceId);
      Log.e(getLogTag(), "Invalid value for " + textField.getHint());
    }

    return 0;
  }

  protected String readEditText(int editTextId) {
    EditText editText = (EditText) getView().findViewById(editTextId);
    return editText.getText().toString();
  }
}
