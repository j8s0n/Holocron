package org.raincitygamers.holocron.rules.abilities;

import org.raincitygamers.holocron.rules.rolls.Bonus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by jason on 1/9/16.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class Talent {
  private final String name;
  private final Bonus bonus;
}
