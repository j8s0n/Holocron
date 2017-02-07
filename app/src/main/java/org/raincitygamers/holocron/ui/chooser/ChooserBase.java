package org.raincitygamers.holocron.ui.chooser;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.ui.ContentPage;

public abstract class ChooserBase extends ContentPage {
  @NotNull
  protected Character getActiveCharacter() {
    return ((ChooserActivity)getActivity()).getActiveCharacter();
  }
}
