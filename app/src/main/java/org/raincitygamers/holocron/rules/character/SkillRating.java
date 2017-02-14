package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.rules.traits.Skill;

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
  private final boolean careerSkill;

  private SkillRating(@NotNull Skill skill, int charScore, int skillScore, boolean careerSkill) {
    this.skill = skill;
    this.charScore = charScore;
    this.skillScore = skillScore;
    this.careerSkill = careerSkill;
    abilityDiceCount = Math.abs(charScore - skillScore);
    proficiencyDiceCount = Math.min(charScore, skillScore);
  }

  public static SkillRating of(@NotNull Skill skill, int charScore, int skillScore, boolean careerSkill) {
    return new SkillRating(skill, charScore, skillScore, careerSkill);
  }

  @NotNull
  public String getRating() {
    StringBuilder sb = new StringBuilder();
    sb.append(" (" + skillScore + ")");
    return sb.toString();
  }
}
