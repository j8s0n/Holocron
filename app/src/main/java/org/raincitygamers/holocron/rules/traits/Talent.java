package org.raincitygamers.holocron.rules.traits;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.CareerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Talent extends Ability {
  private Talent(@NotNull String name, int tier, int row, int column, @NotNull String description) {
    super(name, tier, row, column, description);
  }

  public static Talent of(@NotNull JSONObject o) throws JSONException {
    return new Talent(o.getString(NAME_KEY), o.getInt(TIER_KEY), o.getInt(ROW_KEY), o.getInt(COLUMN_KEY),
                      o.getString(DESCRIPTION_KEY));
  }

  @NotNull
  public static JSONArray toJsonArray(@NotNull Map<Specialization, List<Integer>> talents) throws JSONException {
    JSONArray a = new JSONArray();
    for (Map.Entry<Specialization, List<Integer>> talent : talents.entrySet()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, talent.getKey().getName());
      JSONArray taken = new JSONArray();
      for (Integer i : talent.getValue()) {
        taken.put(i);
      }

      o.put(TALENTS_KEY, taken);
      a.put(o);
    }

    return a;
  }

  @NotNull
  public static Map<Specialization, List<Integer>> parseJsonArray(@NotNull JSONArray jsonArray) throws JSONException {
    Map<Specialization, List<Integer>> talentMap = new HashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject o = jsonArray.getJSONObject(i);
      String specializationName = o.getString(NAME_KEY);
      JSONArray a = o.getJSONArray(TALENTS_KEY);
      List<Integer> talentIndices = new ArrayList<>();
      for (int j = 0; j < a.length(); j++) {
        talentIndices.add(a.getInt(j));
      }

      Collections.sort(talentIndices);
      talentMap.put(CareerManager.getSpecialization(specializationName), talentIndices);
    }

    return talentMap;
  }
}
