package org.raincitygamers.holocron.rules.abilities;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class Ability {
  private static final String NAME_KEY = "name";
  private static final String SOURCE_KEY = "source";
  private static final String TIER_KEY = "tier";
  private static final String DESCRIPTION_KEY = "description";

  private final String name;
  private final String source;
  private final int tier;
  private final String description;

  @NotNull
  public static List<Ability> parseAbilities(@NotNull JSONArray a) throws JSONException {
    List<Ability> abilities = new ArrayList<>();
    for (int i = 0; i < a.length(); i++) {
      abilities.add(Ability.of(a.getJSONObject(i)));
    }

    return abilities;
  }

  @NotNull
  private JSONObject toJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(SOURCE_KEY, source);
    o.put(TIER_KEY, tier);
    o.put(DESCRIPTION_KEY, description);

    return o;
  }

  @NotNull
  public static Ability of(@NotNull JSONObject o) throws JSONException {
    String name = o.getString(NAME_KEY);
    String source = o.getString(SOURCE_KEY);
    int tier = o.getInt(TIER_KEY);
    String description = o.getString(DESCRIPTION_KEY);
    return Ability.of(name, source, tier, description);
  }

  public static JSONArray toJsonArray(List<Ability> abilities) throws JSONException {
    JSONArray a = new JSONArray();
    for (Ability ability : abilities) {
      a.put(ability.toJson());
    }

    return a;
  }
}