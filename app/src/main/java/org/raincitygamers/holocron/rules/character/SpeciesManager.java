package org.raincitygamers.holocron.rules.character;

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

public class SpeciesManager {
  private static final String LOG_TAG = SpeciesManager.class.getSimpleName();
  private static final String SPECIES_LABEL = "species";
  private static final String NAME_KEY = "name";
  private static final String SOAK_BONUS_KEY = "soakBonus";
  private static final String WOUNDS_BONUS_KEY = "woundsBonus";
  private static final String STRAIN_BONUS_KEY = "strainBonus";
  private static SpeciesManager ourInstance = new SpeciesManager();
  private final Map<String, Species> speciesMap = new HashMap<>();

  public static SpeciesManager getInstance() {
    return ourInstance;
  }

  public void addSpecies(@NotNull Species species) {
    speciesMap.put(species.getName(), species);
  }

  @NotNull
  public Species getSpecies(@NotNull String species) {
    return speciesMap.get(species);
  }

  public List<String> getSpeciesNames() {
    List<String> species = new ArrayList<>(speciesMap.keySet());
    Collections.sort(species);
    return species;
  }

  private SpeciesManager() {
    loadSpecies();
  }

  private void loadSpecies() {
    /*
    String speciesJson = FileAccessor.getInstance().getSpeciesContent();
    // Log.i(LOG_TAG, "Species: " + speciesJson);
    try {
      JSONObject o = new JSONObject(speciesJson);
      parseSpecies(o.getJSONArray(SPECIES_LABEL));
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Exception parsing speciesJson", e);
    }
    */
  }

  private void parseSpecies(@NotNull JSONArray speciesJson) {
    for (int i = 0; i < speciesJson.length(); i++) {
      try {
        JSONObject speciesObject = speciesJson.getJSONObject(i);
        String name = speciesObject.getString(NAME_KEY);
        int soakBonus = speciesObject.getInt(SOAK_BONUS_KEY);
        int woundsBonus = speciesObject.getInt(WOUNDS_BONUS_KEY);
        int strainBonus = speciesObject.getInt(STRAIN_BONUS_KEY);

        addSpecies(new Species.Builder(name).soakBonus(soakBonus).woundBonus(woundsBonus).strainBonus(strainBonus)
                       .build());
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Unable to parse Species.json.", e);
      }

    }
  }
}
