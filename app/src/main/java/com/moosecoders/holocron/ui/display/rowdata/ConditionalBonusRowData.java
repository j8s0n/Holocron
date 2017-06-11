package com.moosecoders.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.rules.traits.DicePool.BonusType;
import com.moosecoders.holocron.ui.display.SkillActionEditorActivity.ActionType;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ConditionalBonusRowData implements RowData {
  @Getter private final String condition;
  @Getter private final String skillActionName;
  @Getter private final Map<BonusType, Integer> bonuses;
  @Getter private final ActionType actionType;

  @NotNull
  @Override
  public Type getType() {
    return Type.CONDITIONAL_BONUS;
  }
}
