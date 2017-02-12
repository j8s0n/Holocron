package org.raincitygamers.holocron.ui.display.pages.description;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ContentPage;

public class DescriptionPage extends ContentPage {
  public DescriptionPage() {
  }

  @Override
  public String getTitle() {
    return "Description";
  }

  @Override
  public void onResume() {
    super.onResume();
    TextView description = (TextView) getView().findViewById(R.id.description_text);
    description.setText(CharacterManager.getActiveCharacter().getDescription());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.display_description, container, false);
  }
}
