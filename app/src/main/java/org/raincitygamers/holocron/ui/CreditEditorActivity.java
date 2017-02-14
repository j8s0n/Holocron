package org.raincitygamers.holocron.ui;

import android.os.Bundle;
import android.widget.EditText;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.managers.CharacterManager;

public class CreditEditorActivity extends ActivityBase {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.credit_editor);
    // getSupportActionBar().setTitle("Credit Editor");


    EditText creditEntry = (EditText)findViewById(R.id.credits_entry);
    creditEntry.setText(String.format("%d", CharacterManager.getActiveCharacter().getCredits()));
    creditEntry.requestFocus();
  }

  @Override
  protected void onPause() {
    EditText creditEntry = (EditText)findViewById(R.id.credits_entry);
    CharacterManager.getActiveCharacter().setCredits(Integer.parseInt(creditEntry.getText().toString()));
    super.onPause();
  }
}
