package com.moosecoders.holocron.ui.chooser.pages;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class ScoreRating {
  @Getter private final String scoreLabel;
  @Getter @Setter private int scoreValue;
  @Getter private final int minimumValue;
  @Getter private final int maximumValue;
  @Getter private final boolean careerSkill;

  private ScoreRating(@NotNull String scoreLabel, int scoreValue, int minimumValue, int maximumValue,
                      boolean careerSkill) {
    this.scoreLabel = scoreLabel;
    this.scoreValue = scoreValue;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
    this.careerSkill = careerSkill;
  }

  @NotNull
  public static ScoreRating of(@NotNull String scoreLabel, int scoreValue, int minimumValue, int maximumValue,
                               boolean careerSkill) {
    return new ScoreRating(scoreLabel, scoreValue, minimumValue, maximumValue, careerSkill);
  }
}
