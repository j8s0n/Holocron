package org.raincitygamers.holocron.rules.managers;

import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.character.Career;
import org.raincitygamers.holocron.rules.character.Specialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CareerManager {
  private static final String LOG_TAG = CareerManager.class.getSimpleName();
  private static final String CAREERS_LABEL = "careers";
  private static final String SPECIALIZATIONS_LABEL = "specializations";
  private static final String NAME_KEY = "name";
  private static final String SPECIALIZATIONS_KEY = "specializations";
  private static final String CAREER_SKILLS_KEY = "careerSkills";
  private static Map<String, Career> careerMap = new HashMap<>();
  private static Map<String, Specialization> specializationMap = new HashMap<>();

  static {
    loadCareers();
  }

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

  @Nullable
  public static Career getCareer(@NotNull String name) {
    return careerMap.get(name);
  }

  @NotNull
  public static List<Specialization> getSpecializations() {
    return new ArrayList<>(specializationMap.values());
  }

  @Nullable
  public static Specialization getSpecialization(@NotNull String name) {
    return specializationMap.get(name);
  }

  private static void loadCareers() {
    String careerJson = FileAccessor.getCareerContent();
    // Log.i(LOG_TAG, "Careers: " + careerJson);
    try {
      JSONObject o = new JSONObject(careerJson);
      parseCareers(o.getJSONArray(CAREERS_LABEL));
      parseSpecializations(o.getJSONArray(SPECIALIZATIONS_LABEL));
    } catch (JSONException e) {
      Log.e(LOG_TAG, "Exception parsing careerJson", e);
    }
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
        Log.e(LOG_TAG, "Unable to parse Careers.json.", e);
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
        Log.e(LOG_TAG, "Unable to parse Careers.json.", e);
      }
    }
  }
}
