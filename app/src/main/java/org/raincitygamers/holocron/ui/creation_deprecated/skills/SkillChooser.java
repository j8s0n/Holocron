package org.raincitygamers.holocron.ui.creation_deprecated.skills;

import android.os.Bundle;

import org.raincitygamers.holocron.ui.creation_deprecated.ListChooser;

public class SkillChooser extends ListChooser {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle("Choose Skills");
  }
}
