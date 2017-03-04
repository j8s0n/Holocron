package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.character.Career;
import org.raincitygamers.holocron.rules.character.Specialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CareerManager extends ManagerBase{
  private static final String LOG_TAG = CareerManager.class.getSimpleName();
  private static final String CAREERS_FILE = "Careers.json";
  private static final String CAREERS_LABEL = "careers";
  private static final String SPECIALIZATIONS_LABEL = "specializations";
  private static final String NAME_KEY = "name";
  private static final String SPECIALIZATIONS_KEY = "specializations";
  private static final String CAREER_SKILLS_KEY = "careerSkills";
  private static Map<String, Career> careerMap = new LinkedHashMap<>();
  private static Map<String, Specialization> specializationMap = new HashMap<>();

  private CareerManager() {
  }

  @NotNull
  public static List<String> getCareerNames() {
    List<String> careers = new ArrayList<>(careerMap.keySet());
    Collections.sort(careers);
    return careers;
  }

  @NotNull
  public static List<Career> getCareers() {
    return new ArrayList<>(careerMap.values());
  }

  @NotNull
  public static Career getCareer(@NotNull String name) {
    if (careerMap.containsKey(name)) {
      return careerMap.get(name);
    }
    else if (!careerMap.isEmpty()) {
      return careerMap.values().iterator().next();
    }
    else {
      throw new IllegalStateException("There are no careers in the career manager.");
    }
  }

  @Nullable
  public static Specialization getSpecialization(@NotNull String name) {
    return specializationMap.get(name);
  }

  public static void loadCareers(@NotNull Context context) {
    getFileContent(context, CAREERS_FILE, true, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          JSONObject o = new JSONObject(content);
          CareerManager.parseCareers(o.getJSONArray(CAREERS_LABEL));
          CareerManager.parseSpecializations(o.getJSONArray(SPECIALIZATIONS_LABEL));
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Unable to parse Careers.json.", e);
        }
      }
    });
  }

  @NotNull
  public static Collection<Specialization> getSpecializations() {
    return specializationMap.values();
  }

  private static void parseCareers(@NotNull JSONArray careersJson) {
    for (int i = 0; i < careersJson.length(); i++) {
      ImmutableList.Builder<String> careerSkillsBuilder = ImmutableList.builder();
      ImmutableList.Builder<String> specializationsBuilder = ImmutableList.builder();

      try {
        JSONObject careerObject = careersJson.getJSONObject(i);
        String name = careerObject.getString(NAME_KEY);
        JSONArray specializationsJson = careerObject.getJSONArray(SPECIALIZATIONS_KEY);
        JSONArray careerSkillsJson = careerObject.getJSONArray(CAREER_SKILLS_KEY);
        for (int j = 0; j < specializationsJson.length(); j++) {
          specializationsBuilder.add(specializationsJson.getString(j));
        }

        for (int j = 0; j < careerSkillsJson.length(); j++) {
          careerSkillsBuilder.add(careerSkillsJson.getString(j));
        }

        careerMap.put(name, new Career(name, careerSkillsBuilder.build(), specializationsBuilder.build()));
      } catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Unable to parse Career object at index: %d", i), e);
      }
    }
  }

  private static void parseSpecializations(@NotNull JSONArray specializationsJson) {
    for (int i=0; i < specializationsJson.length(); i++) {
      ImmutableList.Builder<String> careerSkillsBuilder = ImmutableList.builder();

      try {
        JSONObject specializationObject = specializationsJson.getJSONObject(i);
        String name = specializationObject.getString(NAME_KEY);
        JSONArray careerSkillsJson = specializationObject.getJSONArray(CAREER_SKILLS_KEY);
        for (int j = 0; j < careerSkillsJson.length(); j++) {
          careerSkillsBuilder.add(careerSkillsJson.getString(j));
        }

        specializationMap.put(name, new Specialization(name, careerSkillsBuilder.build()));
      } catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Unable to parse Specialization object at index: %d", i), e);
      }
    }
  }
}
