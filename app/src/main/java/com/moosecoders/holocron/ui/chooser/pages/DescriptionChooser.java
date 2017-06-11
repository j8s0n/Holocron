package com.moosecoders.holocron.ui.chooser.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.ui.chooser.ChooserBase;

public class DescriptionChooser extends ChooserBase {
  private static final String LOG_TAG = DescriptionChooser.class.getSimpleName();

  public DescriptionChooser() {
  }

  @Override
  public String getTitle() {
    return "Description";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.choose_description, container, false);
  }

  @Override
  public void onResume() {
    super.onResume();

    View view = getView();
    if (view == null) {
      return;
    }

    EditText description = (EditText) view.findViewById(R.id.description_entry);
    description.setText(getActiveCharacter().getDescription());
  }

  @Override
  public void onPause() {
    super.onPause();

    getActiveCharacter().setDescription(readEditText(R.id.description_entry));
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }
}
