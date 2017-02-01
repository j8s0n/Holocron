package org.raincitygamers.holocron.rules.character;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.abilities.Skill;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jason on 1/9/16.
 */
public class SkillManager {
  private static final String LOG_TAG = SkillManager.class.getSimpleName();
  private static final String SKILLS_LABEL = "skills";
  private static final String NAME_KEY = "name";
  private static final String TYPE_KEY = "type";
  private static final String CHARACTERISTIC_KEY = "characteristic";
  private static SkillManager ourInstance = new SkillManager();
  private final Map<String, Skill> combatSkills = new LinkedHashMap<>();
  private final Map<String, Skill> generalSkills = new LinkedHashMap<>();
  private final Map<String, Skill> knowledgeSkills = new LinkedHashMap<>();

  public static SkillManager getInstance() {
    return ourInstance;
  }

  private SkillManager() {
    loadSkills();
  }

  public void addSkill(@NotNull Skill skill) {
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

  public Skill getSkill(@NotNull String name) {
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
  public Collection<Skill> getCombatSkills() {
    return combatSkills.values();
  }

  @NotNull
  public Collection<Skill> getGeneralSkills() {
    return generalSkills.values();
  }

  @NotNull
  public Collection<Skill> getKnowledgeSkills() {
    return knowledgeSkills.values();
  }

  private void loadSkills() {
    String skillsJson = FileAccessor.getInstance().getSkillContent();
    // Log.i(LOG_TAG, "Skills: " + skillsJson);
    try {
      JSONObject o = new JSONObject(skillsJson);
      parseSkills(o.getJSONArray(SKILLS_LABEL));
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Exception parsing Skills.json", e);
    }
  }

  private void parseSkills(@NotNull JSONArray skillsJson) {
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
}
