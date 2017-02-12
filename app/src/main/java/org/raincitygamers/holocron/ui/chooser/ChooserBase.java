package org.raincitygamers.holocron.ui.chooser;

import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.ui.ContentPage;

public abstract class ChooserBase extends ContentPage {
  @NotNull
  protected Character getActiveCharacter() {
    return ((ChooserActivity)getActivity()).getActiveCharacter();
  }

  protected String readEditText(int editTextId) {
    EditText editText = (EditText) getView().findViewById(editTextId);
    return editText.getText().toString();
  }
}
