package org.raincitygamers.holocron.ui.display.stats;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.ui.display.DisplayCharacterFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsTab extends DisplayCharacterFragment {
  public StatsTab() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_stats, container, false);
  }

}
