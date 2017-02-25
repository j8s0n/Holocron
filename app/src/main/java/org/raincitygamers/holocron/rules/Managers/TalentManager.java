package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.traits.Talent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TalentManager extends ManagerBase{
  private static final String LOG_TAG = TalentManager.class.getSimpleName();
  private static final String TALENTS_FILE = "Talents.json";
  private static final String NAME_KEY = "name";
  private static final String GRAPH_KEY = "graph";

  private static final Map<Specialization, List<Talent>> talentLists = new LinkedHashMap<>();

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

  public static void loadTalents(@NotNull Context context) {
    getFileContent(context, TALENTS_FILE, new ConentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          TalentManager.parseTalents(new JSONArray(content));
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Unable to parse Talents.json.", e);
        }
      }
    });
  }

  private static void parseTalents(@NotNull JSONArray allTalents) {
    for (int i = 0; i < allTalents.length(); i++) {
      try {
        JSONObject specializationTalents = allTalents.getJSONObject(i);
        String specializationName = specializationTalents.getString(NAME_KEY);
        JSONArray talentGraph = specializationTalents.getJSONArray(GRAPH_KEY);
        loadTalent(specializationName, talentGraph);
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Unable to parse Specialization Talents object at index: %d", i), e);
      }
    }
  }

  private static void loadTalent(@NotNull String specializationName, @NotNull JSONArray talentsGraph) {
    List<Talent> talents = new ArrayList<>();
    for (int i = 0; i < talentsGraph.length(); i++) {
      try {
        JSONObject o = talentsGraph.getJSONObject(i);
        talents.add(Talent.of(o));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Error converting talents for: " + specializationName, e);
      }
    }

    talentLists.put(CareerManager.getSpecialization(specializationName), talents);
  }
}
