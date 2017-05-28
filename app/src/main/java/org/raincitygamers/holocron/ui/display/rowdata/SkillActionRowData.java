package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.character.SkillAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SkillActionRowData implements RowData {
  private final SkillAction skillAction;

  @NotNull
  @Override
  public Type getType() {
    return Type.SKILL_ACTION;
  }
}
