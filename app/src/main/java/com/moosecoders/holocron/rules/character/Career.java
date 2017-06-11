package com.moosecoders.holocron.rules.character;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Career {
  private final String name;
  private final ImmutableList<String> careerSkills;
  private final ImmutableList<String> specializations;
  private final ImmutableList<String> prettySpecializations;

  public Career(@NotNull String name, @NotNull ImmutableList<String> careerSkills,
                @NotNull ImmutableList<String> specializations) {
    this.name = name;
    this.careerSkills = careerSkills;
    this.specializations = specializations;
    List<String> prettySpecializationNames = new ArrayList<>();
    for (String specialization : specializations) {
      prettySpecializationNames.add(Specialization.makePretty(specialization));
    }

    prettySpecializations = ImmutableList.copyOf(prettySpecializationNames);
  }
}
