package org.raincitygamers.holocron.rules.managers;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.traits.Talent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TalentManager {
  private static final String LOG_TAG = TalentManager.class.getSimpleName();
  private static final Map<Specialization, List<Talent>> talentLists = new LinkedHashMap<>();

  static {
    loadTalents();
  }

  private TalentManager() {
  }

  @NotNull
  public static List<Talent> getList(@NotNull Specialization specialization) {
    if (talentLists.containsKey(specialization)) {
      return talentLists.get(specialization);
    }
    else {
      // TODO: Throw?
      return Collections.emptyList();
    }
  }

  private static void loadTalents() {
    Map<String, String> talentContent = FileAccessor.getTalentsContent();
    for (Map.Entry<String, String> entry : talentContent.entrySet()) {
      loadTalent(entry.getKey(), entry.getValue());
    }

    for (Specialization specialization : CareerManager.getSpecializations()) {
      if (!talentLists.containsKey(specialization)) {
        Log.w(LOG_TAG, "No talent list for specialization: " + specialization.getName());
        talentLists.put(specialization, new ArrayList<Talent>());
      }
    }
  }


  private static void loadTalent(@NotNull String specializationName, @NotNull String talentContent) {
    JSONArray talentsJson;
    try {
      talentsJson = new JSONArray(talentContent);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading Talents.");
      return;
    }

    List<Talent> talents = new ArrayList<>();
    for (int i = 0; i < talentsJson.length(); i++) {
      try {
        JSONObject o = talentsJson.getJSONObject(i);
        talents.add(Talent.of(o));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Error converting talent: " + talentsJson.toString());
      }
    }

    talentLists.put(CareerManager.getSpecialization(specializationName), talents);
  }
}
