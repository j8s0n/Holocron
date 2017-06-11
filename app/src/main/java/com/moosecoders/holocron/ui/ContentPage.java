package org.raincitygamers.holocron.ui;

import android.app.Fragment;

// The base class for all display and chooser pages.
public abstract class ContentPage extends Fragment {
  public ContentPage() {
  }

  public abstract String getTitle();
}
