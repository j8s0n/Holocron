package com.moosecoders.holocron.ui.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.ActivityBase;

public class WealthEditorActivity extends ActivityBase {
  public static final int EDIT_WEALTH_ENTRY = 0;
  public static final String ID = "ID";
  public static final String LOCATION = "LOCATION";
  public static final String AMOUNT = "AMOUNT";

  private int id;
  private String location;
  private int amount;
  private EditText locationEntry;
  private EditText amountEntry;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.wealth_editor);

    id = getIntent().getIntExtra(ID, -1);
    location = getIntent().getStringExtra(LOCATION);
    amount = getIntent().getIntExtra(AMOUNT, 0);

    locationEntry = (EditText) findViewById(R.id.location);
    amountEntry = (EditText) findViewById(R.id.amount);
    locationEntry.setText(location);
    amountEntry.setText(String.valueOf(amount));
  }

  @Override
  protected String getTitleString() {
    return CharacterManager.getActiveCharacter().getName() + " - Wealth Editor";
  }

  public void onRemoveClicked(View view) {
    Intent resultIntent = new Intent();
    resultIntent.putExtra(ID, id);
    resultIntent.putExtra(LOCATION, "");
    resultIntent.putExtra(AMOUNT, 0);
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }

  public void onDoneClicked(View view) {
    location = locationEntry.getText().toString();
    try {
      amount = Integer.parseInt(amountEntry.getText().toString());
    }
    catch (NumberFormatException e) {
      amount = 0;
    }

    Intent resultIntent = new Intent();
    resultIntent.putExtra(ID, id);
    resultIntent.putExtra(LOCATION, location);
    resultIntent.putExtra(AMOUNT, amount);
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }
}
