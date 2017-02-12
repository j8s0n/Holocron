package org.raincitygamers.holocron.rules.managers;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.traits.ForcePowerUpgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ForcePowerManager {
  private static final String LOG_TAG = ForcePowerManager.class.getSimpleName();
  private static final Map<String, List<ForcePowerUpgrade>> powersList = new LinkedHashMap<>();

  static {
    loadForcePowers();
  }

  private ForcePowerManager() {
  }

  @NotNull
  public static List<ForcePowerUpgrade> getList(@NotNull String chosenPower) {
    if (powersList.containsKey(chosenPower)) {
      return powersList.get(chosenPower);
    }
    else {
      // TODO: Throw?
      return Collections.emptyList();
    }
  }

  private static void loadForcePowers() {
    Map<String, String> forcePowersContent = FileAccessor.getForcePowersContent();
    for (Map.Entry<String, String> entry : forcePowersContent.entrySet()) {
      loadForcePower(entry.getKey(), entry.getValue());
    }
  }

  private static void loadForcePower(@NotNull String powerName, @NotNull String powerContent) {
    JSONArray powerJson;
    try {
      powerJson = new JSONArray(powerContent);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading Force Power:" + powerName);
      return;
    }

    List<ForcePowerUpgrade> powerUpgrades = new ArrayList<>();
    for (int i = 0; i < powerJson.length(); i++) {
      try {
        JSONObject o = powerJson.getJSONObject(i);
        powerUpgrades.add(ForcePowerUpgrade.of(o));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Error converting talent: " + powerJson.toString());
      }
    }

    powersList.put(powerName, powerUpgrades);
  }

  @NotNull
  public static Collection<String> getPowerNames() {
    return powersList.keySet();
  }
}
