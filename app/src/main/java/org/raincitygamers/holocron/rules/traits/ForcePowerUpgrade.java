package org.raincitygamers.holocron.rules.traits;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForcePowerUpgrade extends Ability {
  private static final String LOG_TAG = ForcePowerUpgrade.class.getSimpleName();

  private ForcePowerUpgrade(@NotNull String name, int tier, int row, int column, @NotNull String description) {
    super(name, tier, row, column, description);
  }

  public static ForcePowerUpgrade of(@NotNull String name, int tier, int row, int column, @NotNull String description) {
    return new ForcePowerUpgrade(name, tier, row, column, description);
  }

  public static ForcePowerUpgrade of(@NotNull JSONObject o) throws JSONException {
    return new ForcePowerUpgrade(o.getString(NAME_KEY), o.getInt(TIER_KEY), o.getInt(ROW_KEY), o.getInt(COLUMN_KEY),
                                 o.getString(DESCRIPTION_KEY));
  }

  @NotNull
  public static JSONArray toJsonArray(@NotNull Map<String, List<Integer>> takenForcePowers) throws JSONException {
    JSONArray a = new JSONArray();
    for (Map.Entry<String, List<Integer>> forcePower : takenForcePowers.entrySet()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, forcePower.getKey());
      JSONArray taken = new JSONArray();
      for (Integer i : forcePower.getValue()) {
        taken.put(i);
      }

      o.put(THING_TAKEN_KEY, taken);
      a.put(o);
    }

    return a;
  }

  @NotNull
  public static Map<String, List<Integer>> parseJsonArray(@NotNull JSONArray jsonArray) {
    Map<String, List<Integer>> forcePowersMap = new HashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      String powerName ;
      List<Integer> powerIndices = new ArrayList<>();
      try {
        JSONObject o = jsonArray.getJSONObject(i);
        powerName = o.getString(NAME_KEY);
        JSONArray a = o.getJSONArray(THING_TAKEN_KEY);
        for (int j = 0; j < a.length(); j++) {
          powerIndices.add(a.getInt(j));
        }
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format("Error reading force power at index %d.", i), e);
        continue;
      }

      Collections.sort(powerIndices);
      forcePowersMap.put(powerName, powerIndices);
    }

    return forcePowersMap;
  }
}
