package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.traits.ForcePowerUpgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForcePowerManager extends ManagerBase {
  private static final String LOG_TAG = ForcePowerManager.class.getSimpleName();
  private static final String FORCE_POWERS_FILE = "ForcePowers.json";
  private static final String NAME_KEY = "name";
  private static final String GRAPH_KEY = "graph";

  private static final Map<String, List<ForcePowerUpgrade>> powersList = new LinkedHashMap<>();

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

  public static void loadForcePowers(@NotNull Context context) {
    getFileContent(context, FORCE_POWERS_FILE, new ConentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          ForcePowerManager.parseForcePowers(new JSONArray(content));
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Unable to parse ForcePowers.json.", e);
        }
      }
    });
  }

  private static void parseForcePowers(@NotNull JSONArray allTalents) {
    for (int i = 0; i < allTalents.length(); i++) {
      try {
        JSONObject specializationTalents = allTalents.getJSONObject(i);
        String specializationName = specializationTalents.getString(NAME_KEY);
        JSONArray talentGraph = specializationTalents.getJSONArray(GRAPH_KEY);
        loadForcePower(specializationName, talentGraph);
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Unable to parse Specialization Talents object at index: %d", i), e);
      }
    }
  }

  private static void loadForcePower(@NotNull String powerName, @NotNull JSONArray powerGraph) {
    List<ForcePowerUpgrade> powerUpgrades = new ArrayList<>();
    for (int i = 0; i < powerGraph.length(); i++) {
      try {
        JSONObject o = powerGraph.getJSONObject(i);
        powerUpgrades.add(ForcePowerUpgrade.of(o));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Error converting talent: " + powerGraph.toString());
      }
    }

    powersList.put(powerName, powerUpgrades);
  }

  @NotNull
  public static Collection<String> getPowerNames() {
    return powersList.keySet();
  }
}
