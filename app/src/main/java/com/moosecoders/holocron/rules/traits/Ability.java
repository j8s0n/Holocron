package com.moosecoders.holocron.rules.traits;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

@Getter
public abstract class Ability {
  protected static final String NAME_KEY = "name";
  protected static final String TIER_KEY = "tier";
  protected static final String DESCRIPTION_KEY = "description";
  protected static final String THING_TAKEN_KEY = "thing_taken";
  protected static final String ROW_KEY = "row";
  protected static final String COLUMN_KEY = "column";

  private final String name;
  private final int tier;
  private final int row;
  private final int column;
  private final String description;

  Ability(@NotNull String name, int tier, int row, int column, @NotNull String description) {
    this.name = name;
    this.tier = tier;
    this.row = row;
    this.column = column;
    this.description = description;
  }
}
