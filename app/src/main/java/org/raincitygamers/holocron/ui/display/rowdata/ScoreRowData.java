package org.raincitygamers.holocron.ui.display.rowdata;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.traits.DicePool.BonusType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class ScoreRowData implements RowData {
  @Getter @NonNull private final BonusType bonusType;
  @Getter @NonNull private final int count;
  @Getter @NonNull private final ScoreRowWatcher watcher;

  @NotNull
  @Override
  public Type getType() {
    return Type.SCORE;
  }

  public interface ScoreRowWatcher {
    void valueUpdated(@NotNull BonusType bonusType, int value);
  }
}
