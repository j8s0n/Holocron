package org.raincitygamers.holocron.rules.character;

import org.raincitygamers.holocron.rules.abilities.Skill;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class SkillRating {
  private final Skill skill;
  private final int abilityDiceCount;
  private final int proficiencyDiceCount;

  public String getRating() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < proficiencyDiceCount; i++) {
      sb.append('Y');
    }

    for (int i = 0; i < abilityDiceCount; i++) {
      sb.append('G');
    }

    return sb.toString();
  }
}
