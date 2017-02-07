package org.raincitygamers.holocron.ui.display.pages.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SectionRowData implements RowData {
  @Getter private final String sectionId;

  @NotNull
  @Override
  public Type getType() {
    return Type.SECTION_ID;
  }
}
