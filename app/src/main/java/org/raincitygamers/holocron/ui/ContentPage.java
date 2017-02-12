package org.raincitygamers.holocron.ui;

import android.app.Fragment;
import android.net.Uri;

// The base class for all display and chooser pages.
public abstract class ContentPage extends Fragment {
  public ContentPage() {
  }

  public abstract String getTitle();

  public interface OnFragmentInteractionListener {
    void onNavFragmentInteraction(Uri uri);
  }
}
