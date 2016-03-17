package org.raincitygamers.holocron.rules.character;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by jason on 1/10/16.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class Specialization {
  private final String name;
  private final ImmutableList<String> careerSkills;
}
