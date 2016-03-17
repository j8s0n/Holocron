package org.raincitygamers.holocron.rules.abilities;

import org.jetbrains.annotations.NotNull;

public enum Characteristic {
  BRAWN("Brawn"),
  AGILITY("Agility"),
  INTELLECT("Intellect"),
  CUNNING("Cunning"),
  WILLPOWER("Willpower"),
  PRESENCE("Presence");

  private String value;

  Characteristic(@NotNull String value) {
    this.value = value;
  }

  @NotNull
  public static Characteristic of(@NotNull String value) {
    for (Characteristic characteristic : values()) {
      if (characteristic.toString().equals(value)) {
        return characteristic;
      }
    }

    throw new IllegalArgumentException("Invalid characteristic string.");
  }

  @Override
  public String toString() {
    return value;
  }
}
