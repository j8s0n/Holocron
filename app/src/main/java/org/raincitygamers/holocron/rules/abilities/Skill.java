package org.raincitygamers.holocron.rules.abilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class Skill {
  private final String name;
  private final SkillType skillType;
  private final Characteristic characteristic;
}
