package org.raincitygamers.holocron.ui.pages.skills;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.ui.display.DisplayPage;

public class SkillsPage extends DisplayPage {
  public SkillsPage() {
    // Required empty public constructor
  }

  @Override
  public String getTitle() {
    return "Skills";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_skills, container, false);
  }
}
