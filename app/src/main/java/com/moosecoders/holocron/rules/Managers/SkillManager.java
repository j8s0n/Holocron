package com.moosecoders.holocron.rules.managers;

import android.content.Context;
import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.moosecoders.holocron.rules.traits.Characteristic;
import com.moosecoders.holocron.rules.traits.Skill;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public final class SkillManager extends ManagerBase  {
  private static final String LOG_TAG = SkillManager.class.getSimpleName();
  static final String SKILLS_FILE = "Skills.json";

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
  public static List<Skill> getCombatSkills() {
    return ImmutableList.copyOf(combatSkills.values());
  }

  @NotNull
  public static List<Skill> getGeneralSkills() {
    return ImmutableList.copyOf(generalSkills.values());
  }

  @NotNull
  public static List<Skill> getKnowledgeSkills() {
    return ImmutableList.copyOf(knowledgeSkills.values());
  }

  public static void loadSkills(@NotNull Context context) {
    getFileContent(context, SKILLS_FILE, FileSource.LOCAL_FIRST, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          SkillManager.parseSkills(new JSONArray(content));
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Unable to parse Skills.json.", e);
        }
      }
    });
  }

  public static List<String> getAllSkillNames() {
    List<String> skillNames = new ArrayList<>();
    for (Skill skill : getAllSkills()) {
      skillNames.add(skill.getName());
    }

    return skillNames;
  }

  private static List<Skill> getAllSkills() {
    List<Skill> skills = new ArrayList<>();
    skills.addAll(generalSkills.values());
    skills.addAll(combatSkills.values());
    skills.addAll(knowledgeSkills.values());
    return ImmutableList.copyOf(skills);
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
        Log.e(LOG_TAG, String.format(Locale.US, "Unable to parse Skill object at index: %d", i), e);
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
