package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.abilities.Ability;
import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.abilities.Skill;
import org.raincitygamers.holocron.rules.eote.Obligation;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SectionRowData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Character {
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
  private static final String CHARACTERISTICS_KEY = "characteristics";
  private static final String SKILLS_KEY = "skills";
  private static final String SCORE_KEY = "score";
  private static final String TIMESTAMP_KEY = "last_access_timestamp";
  private static final String ID_KEY = "character_id_key";
  private static final String LAST_OPEN_PAGE_KEY = "last_open_page";

  private static final String WOUNDS_KEY = "wounds";
  private static final String WOUND_THRESHOLD_KEY = "wound_threshold";
  private static final String STRAIN_KEY = "strain";
  private static final String STRAIN_THRESHOLD_KEY = "strain_threshold";

  private static final String MELEE_DEFENSE_KEY = "melee_defense";
  private static final String RANGED_DEFENSE_KEY = "ranged_defense";
  private static final String SOAK_KEY = "soak";
  private static final String ENCUMBRANCE_THRESHOLD_KEY = "encumbrance_threshold";
  private static final String FORCE_RATING_KEY = "force_rating";

  private static final String INVENTORY_KEY = "inventory";
  private static final String TALENTS_KEY = "talents";
  private static final String FORCE_POWERS_KEY = "force_powers";

  @Getter private static final MostRecentAccessComparator mostRecentAccessComparator = new MostRecentAccessComparator();
  @Getter private final List<InventoryItem> inventory = new ArrayList<>();
  @Getter private final List<Ability> talents = new ArrayList<>();
  @Getter private final List<Ability> forcePowers = new ArrayList<>();

  private String logger = Character.class.getSimpleName();
  @Getter @Setter private String name;
  @Getter @Setter private String species;
  @Getter @Setter private Career career;
  @Getter @Setter private final UUID characterId;
  @Getter private final List<Specialization> specializations = new ArrayList<>();
  @Getter private List<Obligation> obligations = new ArrayList<>();
  @Getter @Setter private String age;
  @Getter @Setter private String height;
  @Getter @Setter private String weight;
  @Getter @Setter private String skinTone;
  @Getter @Setter private String hairColor;
  @Getter @Setter private String eyeColor;

  @Getter private int wounds;
  @Getter private int woundThreshold;
  @Getter private int strain;
  @Getter private int strainThreshold;
  @Getter private int meleeDefense;
  @Getter private int rangedDefense;
  @Getter private int soak;
  private int encumbranceThreshold;
  @Getter private int forceRating;

  @Getter
  @Setter
  private int lastOpenPage = 0;
  private long accessTime;

  private final Map<Characteristic, Integer> characteristicScores = new HashMap<>();
  private final Map<Skill, Integer> skills = new HashMap<>();

  public static Character of() {
    return new Character();
  }

  private Character() {
    name = "";
    species = "";
    career = CareerManager.getCareer("Bounty Hunter");
    this.specializations.add(CareerManager.getSpecialization(career.getSpecializations().get(0).toString()));
    this.age = "";
    this.height = "";
    this.weight = "";
    this.skinTone = "";
    this.hairColor = "";
    this.eyeColor = "";
    this.accessTime = 0;
    this.characterId = UUID.randomUUID();

    for (Characteristic characteristic : Characteristic.values()) {
      characteristicScores.put(characteristic, 2);
    }
  }

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
    this.inventory.addAll(builder.inventory);
    this.talents.addAll(builder.talents);
    this.forcePowers.addAll(builder.forcePowers);
    this.wounds = builder.wounds;
    this.woundThreshold = builder.woundThreshold;
    this.strain = builder.strain;
    this.strainThreshold = builder.strainThreshold;
    this.meleeDefense = builder.meleeDefense;
    this.rangedDefense = builder.rangedDefense;
    this.soak = builder.soak;
    this.encumbranceThreshold = builder.encumbranceThreshold;
    this.forceRating = builder.forceRating;
    this.accessTime = builder.accessTime;
    this.characterId = builder.characterId;
    this.lastOpenPage = builder.lastOpenPage;

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
    if (skills.containsKey(skill)) {
      return skills.get(skill);
    }
    else {
      return 0;
    }
  }

  public void setSkillScore(@NotNull Skill skill, int value) {
    skills.put(skill, value);
  }

  public void addSpecialization(@NotNull Specialization specialization) {
    specializations.add(specialization);
  }

  @NotNull
  public List<RowData> getBasics() {
    List<RowData> rowData = new ArrayList<>();
    rowData.addAll(getIdentityBasics());
    rowData.addAll(getCharacteristics());
    rowData.addAll(getDefense());
    return rowData;
  }

  @NotNull
  public List<RowData> getIdentityBasics() {
    List<RowData> rowData = new ArrayList<>();
    rowData.add(KeyValueRowData.of("Name", name));
    rowData.add(KeyValueRowData.of("Species", species));
    rowData.add(KeyValueRowData.of("Career", career.getName()));
    String specializationLabel = "Specialization";
    if (specializations.size() > 1) {
      specializationLabel = "Specializations";
    }

    for (Specialization spec : specializations) {
      rowData.add(KeyValueRowData.of(specializationLabel, spec.getName()));
      specializationLabel = "";
    }

    return rowData;
  }

  @NotNull
  public List<RowData> getCharacteristics() {
    List<RowData> rowData = new ArrayList<>();
    rowData.add(SectionRowData.of("Characteristics"));
    for (Characteristic ch : Characteristic.values()) {
      rowData.add(KeyValueRowData.of(ch.toString(), String.format("%d", getCharacteristicScore(ch))));
    }

    return rowData;
  }

  @NotNull
  public List<RowData> getDefense() {
    List<RowData> rowData = new ArrayList<>();
    rowData.add(SectionRowData.of("Defense"));
    rowData.add(KeyValueRowData.of("Wounds", String.format("%d / %d", wounds, woundThreshold)));
    rowData.add(KeyValueRowData.of("Strain", String.format("%d / %d", strain, strainThreshold)));
    rowData.add(KeyValueRowData.of("Soak", String.format("%d", soak)));
    rowData.add(KeyValueRowData.of("Defense (M/R)", String.format("%d / %d", meleeDefense, rangedDefense)));

    return rowData;
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
    o.put(SPECIES_KEY, species);
    o.put(CAREER_KEY, career.getName());
    o.put(SPECIALIZATIONS_KEY, specializationsAsJsonArray());
    o.put(OBLIGATIONS_KEY, obligationsAsJsonArray());
    o.put(AGE_KEY, age);
    o.put(HEIGHT_KEY, height);
    o.put(WEIGHT_KEY, weight);
    o.put(SKIN_TONE_KEY, skinTone);
    o.put(HAIR_COLOR_KEY, hairColor);
    o.put(EYE_COLOR_KEY, eyeColor);
    o.put(INVENTORY_KEY, InventoryItem.toJsonArray(inventory));
    o.put(TALENTS_KEY, Ability.toJsonArray(talents));
    o.put(FORCE_POWERS_KEY, Ability.toJsonArray(forcePowers));
    o.put(WOUNDS_KEY, wounds);
    o.put(WOUND_THRESHOLD_KEY, woundThreshold);
    o.put(STRAIN_KEY, strain);
    o.put(STRAIN_THRESHOLD_KEY, strainThreshold);
    o.put(MELEE_DEFENSE_KEY, meleeDefense);
    o.put(RANGED_DEFENSE_KEY, rangedDefense);
    o.put(SOAK_KEY, soak);
    o.put(ENCUMBRANCE_THRESHOLD_KEY, encumbranceThreshold);
    o.put(FORCE_RATING_KEY, forceRating);
    o.put(CHARACTERISTICS_KEY, characteristicsAsJsonArray());
    o.put(SKILLS_KEY, skillsAsJsonArray());
    o.put(ID_KEY, characterId);
    o.put(LAST_OPEN_PAGE_KEY, lastOpenPage);
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

  @NotNull
  public static Character valueOf(@NotNull Builder builder) {
    return new Character(builder);
  }

  // Methods for reading from the JSON file.
  @NotNull
  public static Character valueOf(@NotNull JSONObject jsonObject, @NotNull UUID characterId)
  throws IllegalArgumentException, JSONException {
    String name = jsonObject.getString(NAME_KEY);
    Career career = CareerManager.getCareer(jsonObject.getString(CAREER_KEY));
    List<Specialization> specializations = parseSpecializations(jsonObject.getJSONArray(SPECIALIZATIONS_KEY));
    String species = jsonObject.getString(SPECIES_KEY);
    Map<Skill, Integer> skills = parseSkills(jsonObject.getJSONArray(SKILLS_KEY));
    Map<Characteristic, Integer> characteristics = parseCharacteristics(jsonObject.getJSONArray(CHARACTERISTICS_KEY));

    // TODO Robustness on missing fields.
    Character character = new Builder(name, career, specializations.get(0), species, characterId)
                              .age(jsonObject.getString(AGE_KEY))
                              .height(jsonObject.getString(HEIGHT_KEY))
                              .weight(jsonObject.getString(WEIGHT_KEY))
                              .skinTone(jsonObject.getString(SKIN_TONE_KEY))
                              .hairColor(jsonObject.getString(HAIR_COLOR_KEY))
                              .eyeColor(jsonObject.getString(EYE_COLOR_KEY))
                              .inventory(InventoryItem.parseInventory(jsonObject.getJSONArray(INVENTORY_KEY)))
                              .talents(Ability.parseAbilities(jsonObject.getJSONArray(TALENTS_KEY)))
                              .forcePowers(Ability.parseAbilities(jsonObject.getJSONArray(FORCE_POWERS_KEY)))
                              .wounds(jsonObject.getInt(WOUNDS_KEY))
                              .woundThreshold(jsonObject.getInt(WOUND_THRESHOLD_KEY))
                              .strain(jsonObject.getInt(STRAIN_KEY))
                              .strainThreshold(jsonObject.getInt(STRAIN_THRESHOLD_KEY))
                              .meleeDefense(jsonObject.getInt(MELEE_DEFENSE_KEY))
                              .rangedDefense(jsonObject.getInt(RANGED_DEFENSE_KEY))
                              .soak(jsonObject.getInt(SOAK_KEY))
                              .encumbranceThreshold(jsonObject.getInt(ENCUMBRANCE_THRESHOLD_KEY))
                              .forceRating(jsonObject.getInt(FORCE_RATING_KEY))
                              .accessTime(jsonObject.getLong(TIMESTAMP_KEY))
                              .lastOpenPage(jsonObject.getInt(LAST_OPEN_PAGE_KEY))
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
      Skill skill = SkillManager.getSkill(skillJson.getString(NAME_KEY));
      skills.put(skill, skillJson.getInt(SCORE_KEY));
    }

    return skills;
  }

  @NotNull
  private static List<Specialization> parseSpecializations(@NotNull JSONArray jsonArray) throws JSONException {
    List<Specialization> specializations = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      specializations.add(CareerManager.getSpecialization(jsonArray.getString(i)));
    }

    return specializations;
  }

  public Summary makeSummary() {
    return new Summary(characterId, name, species, career.getName(), accessTime);
  }

  public static String buildFileName(@NotNull String characterName, @NotNull UUID characterId) {
    return characterName.replace(' ', '_') + "." + characterId.toString();
  }

  public String getFileName() {
    return buildFileName(name, characterId);
  }

  public int getEncumbrance() {
    int encumbrance = 0;
    for (InventoryItem item : inventory) {
      if (item.isCountEncumbrance() && item.getEncumbrance() > 0) {
        encumbrance += item.getEncumbrance() * item.getQuantity();
      }
    }

    return Math.max(encumbrance, 0);
  }

  public int getEncumbranceThreshold() {
    int threshold = encumbranceThreshold;
    for (InventoryItem item : inventory) {
      if (item.isCountEncumbrance() && item.getEncumbrance() < 0) {
        threshold -= item.getEncumbrance() * item.getQuantity();
      }
    }

    return threshold;
  }

  @Getter
  @ToString
  @EqualsAndHashCode
  @RequiredArgsConstructor(suppressConstructorProperties = true)
  public static class Summary {
    private final UUID characterId;
    private final String name;
    private final String species;
    private final String career;
    private final long timestamp;

    public static Summary valueOf(JSONObject characterJson, UUID characterId) throws JSONException {
      String name = characterJson.getString(NAME_KEY);
      Career career = CareerManager.getCareer(characterJson.getString(CAREER_KEY));
      String species = characterJson.getString(SPECIES_KEY);
      long timestamp = characterJson.getLong(TIMESTAMP_KEY);

      return new Summary(characterId, name, species, career.getName(), timestamp);
    }

    public String getBlurb() {
      return species + " - " + career;
    }

    public String getTimestampString() {
      return new Date(timestamp).toString();
    }

    public String getFileName() {
      return Character.buildFileName(name, characterId);
    }
  }

  public static class Builder {
    private final String name;
    private String age = "";
    private String height = "";
    private String weight = "";
    private String skinTone = "";
    private String hairColor = "";
    private String eyeColor = "";

    private int wounds = 0;
    private int woundThreshold = 10;
    private int strain = 0;
    private int strainThreshold = 10;
    private int meleeDefense = 0;
    private int rangedDefense = 0;
    private int soak = 0;
    private int encumbranceThreshold = 5;
    private int forceRating = 0;

    private long accessTime;
    private int lastOpenPage = 0;
    private List<InventoryItem> inventory = new ArrayList<>();
    private List<Ability> talents = new ArrayList<>();
    private List<Ability> forcePowers = new ArrayList<>();

    private final Career career;
    private final Specialization specialization;
    private final String species;
    private final Map<Characteristic, Integer> characteristics = new HashMap<>();
    private final UUID characterId;


    public Builder(@NotNull String name, @NotNull Career career, @NotNull Specialization specialization,
                   @NotNull String species, @NotNull UUID characterId) {
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
    public Builder wounds(int wounds) {
      this.wounds = wounds;
      return this;
    }

    @NotNull
    public Builder woundThreshold(int woundThreshold) {
      this.woundThreshold = woundThreshold;
      return this;
    }

    @NotNull
    public Builder strain(int strain) {
      this.strain = strain;
      return this;
    }

    @NotNull
    public Builder strainThreshold(int strainThreshold) {
      this.strainThreshold = strainThreshold;
      return this;
    }

    @NotNull
    public Builder meleeDefense(int meleeDefense) {
      this.meleeDefense = meleeDefense;
      return this;
    }

    @NotNull
    public Builder rangedDefense(int rangedDefense) {
      this.rangedDefense = rangedDefense;
      return this;
    }

    @NotNull
    public Builder soak(int soak) {
      this.soak = soak;
      return this;
    }

    @NotNull
    public Builder encumbranceThreshold(int encumbranceThreshold) {
      this.encumbranceThreshold = encumbranceThreshold;
      return this;
    }

    @NotNull
    public Builder forceRating(int forceRating) {
      this.forceRating = forceRating;
      return this;
    }

    @NotNull
    public Builder characteristic(@NotNull Characteristic characteristic, int value) {
      characteristics.put(characteristic, value);
      return this;
    }

    @NotNull
    public Builder lastOpenPage(int lastOpenPage) {
      this.lastOpenPage = lastOpenPage;
      return this;
    }

    @NotNull
    public Character build() {
      return Character.valueOf(this);
    }

    public Builder inventory(List<InventoryItem> inventory) {
      this.inventory = inventory;
      return this;
    }

    public Builder talents(List<Ability> talents) {
      this.talents = talents;
      return this;
    }

    public Builder forcePowers(List<Ability> forcePowers) {
      this.forcePowers = forcePowers;
      return this;
    }
  }

  private static class MostRecentAccessComparator implements Comparator<Character.Summary> {
    @Override
    public int compare(Character.Summary lhs, Character.Summary rhs) {
      return (int) (rhs.timestamp - lhs.timestamp);
    }
  }
}
