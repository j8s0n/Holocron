package org.raincitygamers.holocron.rules.character;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class Career {
  @NonNull private final String name;
  @NonNull private final ImmutableList<String> careerSkills;
  @NonNull private final ImmutableList<String> specializations;
}
