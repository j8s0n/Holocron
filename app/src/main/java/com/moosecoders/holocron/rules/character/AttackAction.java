package com.moosecoders.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import com.moosecoders.holocron.rules.traits.Characteristic;
import com.moosecoders.holocron.rules.traits.DicePool.BonusType;
import com.moosecoders.holocron.rules.traits.Skill;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AttackAction extends SkillAction {
  private static final String DAMAGE_KEY = "damage";
  private static final String CRITICAL_KEY = "critical";
  private static final String RANGE_KEY = "range";
  private static final String TEXT_KEY = "text";

  private final int damage;
  private final int critical;
  private final Range range;
  private final String text;

  private AttackAction(@NotNull String name, @NotNull Characteristic characteristic, @NotNull Skill skill,
                       Map<String, Map<BonusType, Integer>> conditionalBonuses, int damage, int critical,
                       @NotNull Range range, @NotNull String text) {
    super(name, characteristic, skill, conditionalBonuses);
    this.damage = damage;
    this.critical = critical;
    this.range = range;
    this.text = text;
  }

  private AttackAction(@NotNull JSONObject jsonObject) throws JSONException {
    super(jsonObject);
    this. damage = jsonObject.getInt(DAMAGE_KEY);
    this.critical = jsonObject.getInt(CRITICAL_KEY);
    this.range = Range.valueOf(jsonObject.getString(RANGE_KEY));
    this.text = jsonObject.getString(TEXT_KEY);
  }

  @NotNull
  public static AttackAction of(@NotNull String name, @NotNull Characteristic characteristic, @NotNull Skill skill,
                       Map<String, Map<BonusType, Integer>> conditionalBonuses, int damage, int critical,
                       @NotNull Range range, @NotNull String text) {
    return new AttackAction(name, characteristic, skill, conditionalBonuses, damage, critical, range, text);
  }

  @NotNull
  public static AttackAction of(@NotNull JSONObject jsonObject) throws JSONException {
    return new AttackAction(jsonObject);
  }

  @NotNull
  JSONObject toJsonObject() throws JSONException {
    JSONObject o = super.toJsonObject();
    o.put(DAMAGE_KEY, damage);
    o.put(CRITICAL_KEY, critical);
    o.put(RANGE_KEY, range.toString());
    o.put(TEXT_KEY, text);
    return o;
  }

  public enum Range {
    ENGAGED("Engaged"),
    SHORT("Short"),
    MEDIUM("Medium"),
    LONG("Long"),
    EXTREME("Extreme");

    @Getter private final String name;

    Range(String name) {
      this.name = name;
    }

    @NotNull
    public static Range of(@NotNull String name) {
      for (Range range : values()) {
        if (range.getName().equals(name)) {
          return range;
        }
      }

      throw new IllegalArgumentException("Invalid range string: " + name);
    }

  }

  public static class Builder extends SkillAction.Builder {
    @Getter @Setter protected int damage = 0;
    @Getter @Setter protected int critical = 0;
    @Getter @Setter protected Range range = Range.ENGAGED;
    @Getter @Setter protected String text = "";

    @Override
    public SkillAction build() {
      return AttackAction.of(name, characteristic, skill, conditionals, damage, critical, range, text);
    }
  }
}
