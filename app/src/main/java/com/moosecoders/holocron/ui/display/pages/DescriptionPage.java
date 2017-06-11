package com.moosecoders.holocron.ui.display.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.ContentPage;

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
    View view = getView();
    if (view != null) {
      TextView description = (TextView) view.findViewById(R.id.description_text);
      description.setText(CharacterManager.getActiveCharacter().getDescription());
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.display_description, container, false);
  }
}
