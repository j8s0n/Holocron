package org.raincitygamers.holocron.rules.character;

import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.Skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SkillAction {
  private String name;
  private Characteristic characteristic;
  private Skill skill;
  // private List<Bonus> bonuses;
}
