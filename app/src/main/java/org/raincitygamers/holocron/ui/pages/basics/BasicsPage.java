package org.raincitygamers.holocron.ui.pages.basics;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.ui.display.DisplayPage;

// The page that shows name, species, career, etc.
public class BasicsPage extends DisplayPage {
  public BasicsPage() {
    // Required empty public constructor
  }

  @Override
  public String getTitle() {
    return "Basics";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_basics, container, false);
  }
}

// This class and its xml match the page with the Basic label.
// fragment_basics.xml contains a list of fragments to show on the basics tab.
