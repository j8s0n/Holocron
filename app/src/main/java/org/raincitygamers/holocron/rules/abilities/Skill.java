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
  private final Type type;
  private final Characteristic characteristic;

  public enum Type {
    COMBAT,
    GENERAL,
    KNOWLEDGE,
  }
}
