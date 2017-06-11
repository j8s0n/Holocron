package com.moosecoders.holocron.ui.chooser;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.ui.ContentPage;

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
      View view = getView();
      CharSequence field = "";
      if (view != null) {
        EditText textField = (EditText) view.findViewById(resourceId);
        field = textField.getHint();
      }

      Log.e(getLogTag(), "Invalid value for " + field);
    }

    return 0;
  }

  protected String readEditText(int editTextId) {
    View view = getView();
    CharSequence chars = "";
    if (view != null) {
      EditText editText = (EditText) view.findViewById(editTextId);
      return editText.getText().toString();
    }

    return chars.toString();
  }
}
