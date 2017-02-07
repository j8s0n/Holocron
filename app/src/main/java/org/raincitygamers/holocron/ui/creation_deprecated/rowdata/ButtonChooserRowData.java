package org.raincitygamers.holocron.ui.creation_deprecated.rowdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ButtonChooserRowData implements ChooserRowData {
  private final String buttonLabel;
  private final ButtonAction action;

  @Override
  public Type getType() {
    return Type.BUTTON;
  }

  public interface ButtonAction {
    void act();
  }
}
