package org.raincitygamers.holocron.ui.display;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.raincitygamers.holocron.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DisplayCharacterFragment extends Fragment {
  /**
   * The fragment argument representing the section number for this
   * fragment.
   */
  private static final String ARG_SECTION_NUMBER = "section_number";

  public DisplayCharacterFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_display_character, container, false);
    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
    textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
    return rootView;
  }
}
