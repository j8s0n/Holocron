package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.abilities.Skill;
import org.raincitygamers.holocron.rules.eote.Obligation;
import org.raincitygamers.holocron.rules.rolls.DicePool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Character {
  private static final Integer BASE_SOAK = 0;
  private static final Integer BASE_STRAIN = 10;
  private static final Integer BASE_WOUNDS = 10;
  private static final String NAME_KEY = "name";
  private static final String SPECIES_KEY = "species";
  private static final String CAREER_KEY = "career";
  private static final String SPECIALIZATIONS_KEY = "specializations";
  private static final String OBLIGATIONS_KEY = "obligations";
  private static final String AGE_KEY = "age";
  private static final String HEIGHT_KEY = "height";
  private static final String WEIGHT_KEY = "weight";
  private static final String SKIN_TONE_KEY = "skinTone";
  private static final String HAIR_COLOR_KEY = "hairColor";
  private static final String EYE_COLOR_KEY = "eyeColor";
  private static final String CHARACTERISTIC_KEY = "characteristic";
  private static final String CHARACTERISTICS_KEY = "characteristics";
  private static final String SKILLS_KEY = "skills";
  private static final String SKILL_TYPE_KEY = "skillType";
  private static final String SCORE_KEY = "score";
  private static final String TIMESTAMP_KEY = "last_access_timestamp";
  private static final String ID_KEY = "character_id_key";
  private static final CareerManager careerManager = CareerManager.getInstance();
  private static final CharacterManager characterManager = CharacterManager.getInstance();
  private static final SkillManager skillManager = SkillManager.getInstance();
  private static final SpeciesManager speciesManager = SpeciesManager.getInstance();
  @Getter private static final MostRecentAccessComparator mostRecentAccessComparator = new MostRecentAccessComparator();

  private String logger = Character.class.getSimpleName();
  @Getter private String name;
  @Getter private final Species species;
  @Getter private final Career career;
  @Getter private final UUID characterId;
  @Getter private final Set<Specialization> specializations = new HashSet<>();
  @Getter private List<Obligation> obligations = new ArrayList<>();
  @Getter private String age;
  @Getter private String height;
  @Getter private String weight;
  @Getter private String skinTone;
  @Getter private String hairColor;
  @Getter private String eyeColor;
  private long accessTime;

  private final Map<Characteristic, Integer> characteristicScores = new HashMap<>();
  private final Map<Skill, Integer> skills = new HashMap<>();
  private final Map<Skill, DicePool> skillPools = new HashMap<>();

  // TODO:
  // For bonuses, have a map->list for each thing that can have bonuses (e.g. brawn->list<bonuses>).
  // List the source of the bonus, for easy removal later, if that changes.
  // When calculating the thing, walk the list of its bonuses.
  private Character(@NotNull Builder builder) {
    this.name = builder.name;
    this.species = builder.species;
    this.career = builder.career;
    this.specializations.add(builder.specialization);
    this.age = builder.age;
    this.height = builder.height;
    this.weight = builder.weight;
    this.skinTone = builder.skinTone;
    this.hairColor = builder.hairColor;
    this.eyeColor = builder.eyeColor;
    this.accessTime = builder.accessTime;
    this.characterId = builder.characterId;

    buildDicePools(skillManager.getCombatSkills(), skillPools);
    buildDicePools(skillManager.getGeneralSkills(), skillPools);
    buildDicePools(skillManager.getKnowledgeSkills(), skillPools);
    for (Map.Entry<Characteristic, Integer> entry : builder.characteristics.entrySet()) {
      characteristicScores.put(entry.getKey(), entry.getValue());
    }
  }

  public int getCharacteristicScore(@NotNull Characteristic characteristic) {
    return characteristicScores.get(characteristic);
  }

  public void setCharacteristicScore(@NotNull Characteristic characteristic, int value) {
    characteristicScores.put(characteristic, value);
  }

  public int getSkillScore(@NotNull Skill skill) {
    return skills.get(skill);
  }

  public void setSkillScore(@NotNull Skill skill, int value) {
    skills.put(skill, value);
  }

  public void addSpecialization(@NotNull Specialization specialization) {
    specializations.add(specialization);
  }

  public int getSoak() {
    return BASE_SOAK + characteristicScores.get(Characteristic.BRAWN) + species.getSoakBonus();
  }

  public int getMaxWounds() {
    return BASE_WOUNDS + characteristicScores.get(Characteristic.BRAWN) + species.getWoundsBonus();
  }

  public int getMaxStrain() {
    return BASE_STRAIN + characteristicScores.get(Characteristic.WILLPOWER) + species.getStrainBonus();
  }

  public int getMeleeDefense() {
    return 0;
  }

  public int getRangedDefense() {
    return 0;
  }

  private void buildDicePools(@NotNull Collection<Skill> skills, @NotNull Map<Skill, DicePool> dicePools) {
    for (Skill skill : skills) {
      dicePools.put(skill, new DicePool(skill.getCharacteristic(), skill));
    }
  }

  public void update() {
    updateDicePools(skillPools.values());
  }

  private void updateDicePools(@NotNull Collection<DicePool> dicePools) {
    for (DicePool pool : dicePools) {
      pool.recalculatePool(characteristicScores.get(pool.getCharacteristic()), getSkillScore(pool.getSkill()));
    }
  }

  public long getTimestamp() {
    return accessTime;
  }

  public void updateTimestamp() {
    accessTime = System.currentTimeMillis();
  }

  public String getTimestampString() {
    return new Date(accessTime).toString();
  }

  // Methods for writing to the JSON file.
  @NotNull
  public JSONObject toJsonObject() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(SPECIES_KEY, species.getName());
    o.put(CAREER_KEY, career.getName());
    o.put(SPECIALIZATIONS_KEY, specializationsAsJsonArray());
    o.put(OBLIGATIONS_KEY, obligationsAsJsonArray());
    o.put(AGE_KEY, age);
    o.put(HEIGHT_KEY, height);
    o.put(WEIGHT_KEY, weight);
    o.put(SKIN_TONE_KEY, skinTone);
    o.put(HAIR_COLOR_KEY, hairColor);
    o.put(EYE_COLOR_KEY, eyeColor);
    o.put(CHARACTERISTICS_KEY, characteristicsAsJsonArray());
    o.put(SKILLS_KEY, skillsAsJsonArray());
    o.put(ID_KEY, characterId);
    o.put(TIMESTAMP_KEY, accessTime);
    return o;
  }

  @NotNull
  private JSONArray characteristicsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (Characteristic c : Characteristic.values()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, c.toString());
      o.put(SCORE_KEY, getCharacteristicScore(c));
      a.put(o);
    }

    return a;
  }

  @NotNull
  private JSONArray obligationsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (Obligation obligation : obligations) {
      JSONObject o = new JSONObject();
      o.put(obligation.getDescription(), obligation.getValue());
      a.put(o);
    }

    return a;
  }

  @NotNull
  private JSONArray skillsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    putSkillsInJsonArray(a, skills);
    return a;
  }

  @NotNull
  private void putSkillsInJsonArray(@NotNull JSONArray a, @NotNull Map<Skill, Integer> skillMap) throws JSONException {
    for (Map.Entry<Skill, Integer> entry : skillMap.entrySet()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, entry.getKey().getName());
      o.put(SCORE_KEY, entry.getValue());
      a.put(o);
    }
  }

  @NotNull
  private JSONArray specializationsAsJsonArray() {
    JSONArray a = new JSONArray();
    for (Specialization specialization : specializations) {
      a.put(specialization.getName());
    }

    return a;
  }

  // Methods for reading from the JSON file.
  @NotNull
  public static Character valueOf(@NotNull JSONObject jsonObject, @NotNull UUID characterId)
  throws IllegalArgumentException, JSONException {
    String name = jsonObject.getString(NAME_KEY);
    Career career = careerManager.getCareer(jsonObject.getString(CAREER_KEY));
    List<Specialization> specializations = parseSpecializations(jsonObject.getJSONArray(SPECIALIZATIONS_KEY));
    Species species = speciesManager.getSpecies(jsonObject.getString(SPECIES_KEY));
    Map<Skill, Integer> skills = parseSkills(jsonObject.getJSONArray(SKILLS_KEY));
    Map<Characteristic, Integer> characteristics = parseCharacteristics(jsonObject.getJSONArray(CHARACTERISTICS_KEY));

    Character character = new Builder(name, career, specializations.get(0), species, characterId)
                              .age(jsonObject.getString(AGE_KEY))
                              .height(jsonObject.getString(HEIGHT_KEY))
                              .weight(jsonObject.getString(WEIGHT_KEY))
                              .skinTone(jsonObject.getString(SKIN_TONE_KEY))
                              .hairColor(jsonObject.getString(HAIR_COLOR_KEY))
                              .eyeColor(jsonObject.getString(EYE_COLOR_KEY))
                              .accessTime(jsonObject.getLong(TIMESTAMP_KEY))
                              .build();
    for (Map.Entry<Skill, Integer> skill : skills.entrySet()) {
      character.setSkillScore(skill.getKey(), skill.getValue());
    }

    for (Map.Entry<Characteristic, Integer> characteristic : characteristics.entrySet()) {
      character.setCharacteristicScore(characteristic.getKey(), characteristic.getValue());
    }

    for (Specialization specialization : specializations.subList(1, specializations.size())) {
      character.addSpecialization(specialization);
    }
    // TODO:
    // Obligation

    return character;
  }

  @NotNull
  private static Map<Characteristic, Integer> parseCharacteristics(@NotNull JSONArray jsonArray) throws JSONException {
    Map<Characteristic, Integer> characteristics = new HashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject characteristicJson = jsonArray.getJSONObject(i);
      Characteristic characteristic = Characteristic.of(characteristicJson.getString(NAME_KEY));
      characteristics.put(characteristic, characteristicJson.getInt(SCORE_KEY));
    }

    return characteristics;
  }

  @NotNull
  private static Map<Skill, Integer> parseSkills(@NotNull JSONArray jsonArray) throws JSONException {
    Map<Skill, Integer> skills = new HashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject skillJson = jsonArray.getJSONObject(i);
      Skill skill = skillManager.getSkill(skillJson.getString(NAME_KEY));
      skills.put(skill, skillJson.getInt(SCORE_KEY));
    }

    return skills;
  }

  @NotNull
  private static List<Specialization> parseSpecializations(@NotNull JSONArray jsonArray) throws JSONException {
    List<Specialization> specializations = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      specializations.add(careerManager.getSpecialization(jsonArray.getString(i)));
    }

    return specializations;
  }

  public static class Builder {
    private final String name;
    private String age = "";
    private String height = "";
    private String weight = "";
    private String skinTone = "";
    private String hairColor = "";
    private String eyeColor = "";
    private long accessTime;
    private final Career career;
    private final Specialization specialization;
    private final Species species;
    private final Map<Characteristic, Integer> characteristics = new HashMap<>();
    private final UUID characterId;

    public Builder(@NotNull String name, @NotNull Career career, @NotNull Specialization specialization,
                   @NotNull Species species, @NotNull UUID characterId) {
      this.name = name;
      this.career = career;
      this.specialization = specialization;
      this.species = species;
      this.accessTime = System.currentTimeMillis();
      this.characterId = characterId;
    }

    @NotNull
    public Builder accessTime(long accessTime) {
      this.accessTime = accessTime;
      return this;
    }

    @NotNull
    public Builder age(@NotNull String age) {
      this.age = age;
      return this;
    }

    @NotNull
    public Builder height(@NotNull String height) {
      this.height = height;
      return this;
    }

    @NotNull
    public Builder weight(@NotNull String weight) {
      this.weight = weight;
      return this;
    }

    @NotNull
    public Builder skinTone(@NotNull String skinTone) {
      this.skinTone = skinTone;
      return this;
    }

    @NotNull
    public Builder hairColor(@NotNull String hairColor) {
      this.hairColor = hairColor;
      return this;
    }

    @NotNull
    public Builder eyeColor(@NotNull String eyeColor) {
      this.eyeColor = eyeColor;
      return this;
    }

    @NotNull
    public Builder characteristic(@NotNull Characteristic characteristic, int value) {
      characteristics.put(characteristic, Integer.valueOf(value));
      return this;
    }

    @NotNull
    public Character build() {
      return new Character(this);
    }
  }

  private static class MostRecentAccessComparator implements Comparator<Character> {
    @Override
    public int compare(Character lhs, Character rhs) {
      return (int) (rhs.accessTime - lhs.accessTime);
    }
  }
}
