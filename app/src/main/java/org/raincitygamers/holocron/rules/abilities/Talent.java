package org.raincitygamers.holocron.rules.abilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class Talent {
  private final String name;
  private final String description;
}
