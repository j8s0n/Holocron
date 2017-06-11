package com.moosecoders.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.rules.character.AttackAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class AttackActionRowData implements RowData {
  private final AttackAction attackAction;

  @NotNull
  @Override
  public Type getType() {
    return Type.ATTACK_ACTION;
  }
}
