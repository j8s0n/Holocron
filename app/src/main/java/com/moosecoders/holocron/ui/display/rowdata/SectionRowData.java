package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SectionRowData implements RowData {
  @Getter @NonNull private final String sectionId;
  @Getter @NonNull private final String containerPage;
  @Getter @Setter private boolean collapsed;

  @NotNull
  @Override
  public Type getType() {
    return Type.SECTION_ID;
  }
}
