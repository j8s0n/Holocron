package com.moosecoders.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.rules.character.SkillAction;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SkillActionRowData implements RowData {
  @NonNull private final SkillAction skillAction;

  @NotNull
  @Override
  public Type getType() {
    return Type.SKILL_ACTION;
  }
}
