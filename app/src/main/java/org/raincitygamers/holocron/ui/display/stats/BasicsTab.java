package org.raincitygamers.holocron.ui.display.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.raincitygamers.holocron.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicsTab extends Fragment {
  public BasicsTab() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_basics_tab, container, false);
    return view;
  }
}
