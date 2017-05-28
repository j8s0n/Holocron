package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SectionRowData implements RowData {
  @Getter private final String sectionId;
  @Getter private final String containerPage;

  @NotNull
  @Override
  public Type getType() {
    return Type.SECTION_ID;
  }
}
