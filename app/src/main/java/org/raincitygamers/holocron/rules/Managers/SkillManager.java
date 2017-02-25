package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.Skill;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SkillManager {
  private static final String LOG_TAG = SkillManager.class.getSimpleName();
  private static final String SKILLS_FILE = "Skills.json";
  private static final String SKILLS_URL = "https://raw.githubusercontent.com/j8s0n/Holocron/master/DataFiles/Skills.json";

  private static final String SKILLS_LABEL = "skills";
  private static final String NAME_KEY = "name";
  private static final String TYPE_KEY = "type";
  private static final String CHARACTERISTIC_KEY = "characteristic";
  private static final Map<String, Skill> combatSkills = new LinkedHashMap<>();
  private static final Map<String, Skill> generalSkills = new LinkedHashMap<>();
  private static final Map<String, Skill> knowledgeSkills = new LinkedHashMap<>();

  private SkillManager() {
  }

  public static Skill getSkill(@NotNull String name) {
    if (combatSkills.containsKey(name)) {
      return combatSkills.get(name);
    }
    else if (generalSkills.containsKey(name)) {
      return generalSkills.get(name);
    }
    else if (knowledgeSkills.containsKey(name)) {
      return knowledgeSkills.get(name);
    }
    else {
      Log.e(LOG_TAG, "Invalid skill: " + name);
      throw new IllegalArgumentException("Invalid skill: " + name);
    }
  }

  @NotNull
  public static Collection<Skill> getCombatSkills() {
    return combatSkills.values();
  }

  @NotNull
  public static Collection<Skill> getGeneralSkills() {
    return generalSkills.values();
  }

  @NotNull
  public static Collection<Skill> getKnowledgeSkills() {
    return knowledgeSkills.values();
  }

  public static void loadSkills(@NotNull Context context) {
    File x = context.getFilesDir();
    try {
      parseSkills(new JSONObject(getSkillContent(context)).getJSONArray(SKILLS_LABEL));
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Exception parsing Skills.json", e);
    }
  }

  @NotNull
  private static String getSkillContent(@NotNull final Context context) {
    if (!Arrays.asList(context.fileList()).contains(SKILLS_FILE)) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          String content = downloadLatestSkillsFile(context);
          try {
            parseSkills(new JSONObject(content).getJSONArray(SKILLS_LABEL));
          }
          catch (JSONException e) {
            Log.e(LOG_TAG, "Exception parsing Skills.json from the interwebs.", e);
          }
        }
      });
    }
    else {
      try {
        FileInputStream fis = context.openFileInput(SKILLS_FILE);
        String input = readInputStream(fis);
        fis.close();
        return input;
      }
      catch (FileNotFoundException e) {
        Log.e(LOG_TAG, "Unable to find file: " + SKILLS_FILE, e);
      }
      catch (IOException e) {
        Log.e(LOG_TAG, "Unable to read or close file: " + SKILLS_FILE, e);
      }
    }

    return "";
  }

  private static String downloadLatestSkillsFile(@NotNull Context context) {
    try {
      InputStream fis = new URL(SKILLS_URL).openStream();
      String content = readInputStream(fis);
      fis.close();

      FileOutputStream fos = context.openFileOutput(SKILLS_FILE, Context.MODE_PRIVATE);
      fos.write(content.getBytes());
      fos.close();

      return content;
    }
    catch (MalformedURLException e) {
      Log.e(LOG_TAG, "Malformed URL: " + SKILLS_URL, e);
    }
    catch (IOException e) {
      Log.e(LOG_TAG, "Stream error.", e);
    }

    return "";
  }

  private static String readInputStream(@NotNull InputStream is) throws IOException {
    byte[] buffer = new byte[65536];
    StringBuilder sb = new StringBuilder();
    int count;
    while ((count = is.read(buffer)) >= 0) {
      sb.append(new String(buffer, 0, count));
    }

    is.close();
    return sb.toString();
  }

  private static void parseSkills(@NotNull JSONArray skillsJson) {
    for (int i = 0; i < skillsJson.length(); i++) {
      try {
        JSONObject skillObject = skillsJson.getJSONObject(i);
        String name = skillObject.getString(NAME_KEY);
        Characteristic characteristic = Characteristic.of(skillObject.getString(CHARACTERISTIC_KEY));
        String skillType = skillObject.getString(TYPE_KEY).toUpperCase();

        addSkill(new Skill(name, Skill.Type.valueOf(skillType), characteristic));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Unable to parse Skills.json.", e);
      }

    }
  }

  private static void addSkill(@NotNull Skill skill) {
    switch (skill.getType()) {
    case COMBAT:
      combatSkills.put(skill.getName(), skill);
      break;
    case GENERAL:
      generalSkills.put(skill.getName(), skill);
      break;
    case KNOWLEDGE:
      knowledgeSkills.put(skill.getName(), skill);
      break;
    default:
      throw new IllegalArgumentException("Invalid skill type.");
    }
  }
}
