package org.raincitygamers.holocron.ui;

import android.app.Fragment;
import android.net.Uri;

// The base class for all display pages.
public abstract class ContentPage extends Fragment {
  // private static final String ARG_SECTION_NUMBER = "section_number";

  public ContentPage() {
  }

  public abstract String getTitle();

  /*
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //View rootView = inflater.inflate(R.layout.fragment_display_character, container, false);
    //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
    //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
    return rootView;
  }
  */

  public interface OnFragmentInteractionListener {
    void onNavFragmentInteraction(Uri uri);
  }
}
