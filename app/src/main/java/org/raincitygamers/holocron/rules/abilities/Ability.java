package org.raincitygamers.holocron.rules.abilities;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

@Getter
public abstract class Ability {
  protected static final String NAME_KEY = "name";
  protected static final String TIER_KEY = "tier";
  protected static final String DESCRIPTION_KEY = "description";
  protected static final String TALENTS_KEY = "talents_taken";

  private final String name;
  private final int tier;
  private final String description;

  protected Ability(@NotNull String name, int tier, @NotNull String description) {
    this.name = name;
    this.tier = tier;
    this.description = description;
  }
}
