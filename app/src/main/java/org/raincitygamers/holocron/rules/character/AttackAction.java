package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;

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

  /*
  private AttackAction(@NotNull String name, @NotNull Characteristic characteristic, @NotNull Skill skill,
                       int damage, int critical, @NotNull Range range, @NotNull String text) {
    super(name, characteristic, skill, Collections.EMPTY_MAP);
    this.damage = damage;
    this.critical = critical;
    this.range = range;
    this.text = text;
  }
  */

  private AttackAction(@NotNull JSONObject jsonObject) throws JSONException {
    super(jsonObject);
    this. damage = jsonObject.getInt(DAMAGE_KEY);
    this.critical = jsonObject.getInt(CRITICAL_KEY);
    this.range = Range.valueOf(jsonObject.getString(RANGE_KEY));
    this.text = jsonObject.getString(TEXT_KEY);
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
  }
}
