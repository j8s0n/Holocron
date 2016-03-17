package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by jason on 1/9/16.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Species {
  private final String name;
  private final int soakBonus;
  private final int woundsBonus;
  private final int strainBonus;

  private Species(@NotNull Builder builder) {
    this.name = builder.name;
    this.soakBonus = builder.soakBonus;
    this.woundsBonus = builder.woundBonus;
    this.strainBonus = builder.strainBonus;
  }

  public static class Builder {
    private final String name;
    private int soakBonus;
    private int woundBonus;
    private int strainBonus;

    public Builder(@NotNull String name) {
      this.name = name;
    }

    @NotNull
    public Builder soakBonus(int soakBonus) {
      this.soakBonus = soakBonus;
      return this;
    }

    @NotNull
    public Builder woundBonus(int woundBonus) {
      this.woundBonus = woundBonus;
      return this;
    }

    @NotNull
    public Builder strainBonus(int strainBonus) {
      this.strainBonus = strainBonus;
      return this;
    }

    @NotNull
    public Species build() {
      return new Species(this);
    }
  }
}
