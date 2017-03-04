package org.raincitygamers.holocron.rules.character;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Specialization implements Comparable <Specialization> {
  private final String name;
  private final String prettyName;
  private final String longPrettyName;
  private final ImmutableList<String> careerSkills;

  public Specialization(@NotNull String name, @NotNull ImmutableList<String> careerSkills) {
    this.name = name;
    this.careerSkills = careerSkills;
    this.prettyName = makePretty(name);
    this.longPrettyName = name.replace("--", ": ");
  }

  static String makePretty(@NotNull String specialization) {
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

  @Override
  public int compareTo(@NonNull Specialization o) {
    return name.compareTo(o.name);
  }
}
