package org.raincitygamers.holocron.rules.character;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Specialization {
  private final String name;
  private final String prettyName;
  private final ImmutableList<String> careerSkills;

  public Specialization(@NotNull String name, @NotNull ImmutableList<String> careerSkills) {
    this.name = name;
    this.careerSkills = careerSkills;
    this.prettyName = makePretty(name);
  }

  public static String makePretty(@NotNull String specialization) {
    int doubleDash = specialization.indexOf("--");
    String prettyName;
    if (doubleDash > -1) {
      prettyName = specialization.substring(doubleDash + 2);
    }
    else {
      prettyName = specialization;
    }

    return prettyName;
  }
}
