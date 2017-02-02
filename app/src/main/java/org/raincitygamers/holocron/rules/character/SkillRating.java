package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.abilities.Skill;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SkillRating {
  private final Skill skill;
  private final int charScore;
  private final int skillScore;
  private final int abilityDiceCount;
  private final int proficiencyDiceCount;

  public SkillRating(@NotNull Skill skill, int charScore, int skillScore) {
    this.skill = skill;
    this.charScore = charScore;
    this.skillScore = skillScore;
    abilityDiceCount = Math.abs(charScore - skillScore);
    proficiencyDiceCount = Math.min(charScore, skillScore);
  }

  public String getRating() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < proficiencyDiceCount; i++) {
      sb.append('Y');
    }

    for (int i = 0; i < abilityDiceCount; i++) {
      sb.append('G');
    }

    sb.append(" (" + skillScore + ")");
    return sb.toString();
  }
}
