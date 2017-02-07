package org.raincitygamers.holocron.ui.chooser.pages.scoregroup;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class ScoreRating {
  @Getter private final String scoreLabel;
  @Getter @Setter private int scoreValue;
  @Getter private final int minimumValue;
  @Getter private final int maximumValue;

  private ScoreRating(@NotNull String scoreLabel, int scoreValue, int minimumValue, int maximumValue) {
    this.scoreLabel = scoreLabel;
    this.scoreValue = scoreValue;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
  }

  @NotNull
  public static ScoreRating of(@NotNull String scoreLabel, int scoreValue, int minimumValue, int maximumValue) {
    return new ScoreRating(scoreLabel, scoreValue, minimumValue, maximumValue);
  }
}
