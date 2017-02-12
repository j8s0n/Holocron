package org.raincitygamers.holocron.rules.traits;

import org.jetbrains.annotations.NotNull;

public enum Characteristic {
  BRAWN("Brawn", "Br"),
  AGILITY("Agility", "Ag"),
  INTELLECT("Intellect", "Int"),
  CUNNING("Cunning", "Cun"),
  WILLPOWER("Willpower", "Wil"),
  PRESENCE("Presence", "Pr");

  private String name;
  private String abbreviation;

  Characteristic(@NotNull String name, @NotNull String abbreviation) {
    this.name = name;
    this.abbreviation = abbreviation;
  }

  @NotNull
  public static Characteristic of(@NotNull String name) {
    for (Characteristic characteristic : values()) {
      if (characteristic.toString().equals(name)) {
        return characteristic;
      }
    }

    throw new IllegalArgumentException("Invalid characteristic string.");
  }

  @Override
  public String toString() {
    return name;
  }

  public String getAbbreviation() {
    return abbreviation;
  }
}
