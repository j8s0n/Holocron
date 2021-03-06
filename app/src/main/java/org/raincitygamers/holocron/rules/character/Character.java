package org.raincitygamers.holocron.rules.character;

import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.raincitygamers.holocron.rules.managers.CareerManager;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.ForcePowerUpgrade;
import org.raincitygamers.holocron.rules.traits.Obligation;
import org.raincitygamers.holocron.rules.traits.Skill;
import org.raincitygamers.holocron.rules.traits.Talent;
import org.raincitygamers.holocron.ui.display.rowdata.AttackActionRowData;
import org.raincitygamers.holocron.ui.display.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.SectionRowData;
import org.raincitygamers.holocron.ui.display.rowdata.SkillActionRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ThresholdRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ToggleRowData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Character {
  private static final String LOG_TAG = Character.class.getSimpleName();

  private static final String NAME_KEY = "name";
  private static final String SPECIES_KEY = "species";
  private static final String CAREER_KEY = "career";
  private static final String SPECIALIZATIONS_KEY = "specializations";
  private static final String PRIMARY_KEY = "primary";
  private static final String SECONDARY_KEY = "secondary";
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
  private static final String ID_KEY = "character_id";
  private static final String LAST_OPEN_PAGE_KEY = "last_open_page";
  private static final String XP_KEY = "xp";
  private static final String WEALTH_KEY = "wealth";

  private static final String WOUNDS_KEY = "wounds";
  private static final String WOUND_THRESHOLD_KEY = "wound_threshold";
  private static final String STRAIN_KEY = "strain";
  private static final String STRAIN_THRESHOLD_KEY = "strain_threshold";

  private static final String MELEE_DEFENSE_KEY = "melee_defense";
  private static final String RANGED_DEFENSE_KEY = "ranged_defense";
  private static final String SOAK_KEY = "soak";
  private static final String FORCE_RATING_KEY = "force_rating";

  private static final String INVENTORY_KEY = "inventory";
  private static final String TALENTS_KEY = "talents";
  private static final String FORCE_POWERS_KEY = "force_powers";
  private static final String DESCRIPTION_KEY = "description";
  private static final String ACTION_CONDITIONS_KEY = "action_conditions";
  private static final String SKILL_ACTIONS_KEY = "skill_actions";
  private static final String ATTACK_ACTIONS_KEY = "attack_actions";
  private static final String SET_KEY = "set";
  private static final String HIDDEN_SECTIONS_KEY = "hidden_sections";
  private static final String SECTION_KEY = "section";
  private static final String PAGE_KEY = "page";
  private static final String VALUE_KEY = "value";

  @Getter private static final MostRecentAccessComparator mostRecentAccessComparator = new MostRecentAccessComparator();
  @Getter private final List<InventoryItem> inventory = new ArrayList<>();
  private final Map<Specialization, List<Integer>> talents = new LinkedHashMap<>();
  @Getter private final Map<String, List<Integer>> forcePowers = new LinkedHashMap<>();

  private String logger = Character.class.getSimpleName();
  @Getter @Setter private String name;
  @Getter @Setter private String species;
  @Getter @Setter private Career career;
  @Getter private final UUID characterId;
  @Getter private Specialization primarySpecialization;
  @Getter private final Set<Specialization> secondarySpecializations = new LinkedHashSet<>();
  @Getter private List<Obligation> obligations = new ArrayList<>();
  private Map<String, Boolean> actionConditions = new HashMap<>();
  private Map<String, SkillAction> skillActions = new LinkedHashMap<>();
  private Map<String, AttackAction> attackActions = new LinkedHashMap<>();
  @Getter private Map<String, Integer> wealth = new LinkedHashMap<>();

  @Getter @Setter private int age;
  @Getter @Setter private String height;
  @Getter @Setter private String weight;
  @Getter @Setter private String skinTone;
  @Getter @Setter private String hairColor;
  @Getter @Setter private String eyeColor;

  @Getter @Setter private String description;

  @Getter private int wounds;
  @Getter @Setter private int woundThreshold;
  @Getter private int strain;
  @Getter @Setter private int strainThreshold;
  @Getter @Setter private int meleeDefense;
  @Getter @Setter private int rangedDefense;
  @Getter @Setter private int soak;
  @Getter @Setter private int forceRating;

  @Getter @Setter private int lastOpenPage = 0;
  @Getter @Setter private int xp;
  private long accessTime;

  private final Set<HiddenSection> hiddenSections = new HashSet<>();
  private final Map<Characteristic, Integer> characteristicScores = new HashMap<>();
  private final Map<Skill, Integer> skills = new HashMap<>();

  public static Character of() {
    return new Character();
  }

  private Character() {
    name = "";
    species = "";
    career = CareerManager.getCareers().get(0);
    this.primarySpecialization = CareerManager.getSpecialization(career.getSpecializations().get(0));
    this.age = 0;
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

  private Character(@NotNull Builder builder) {
    this.name = builder.name;
    this.species = builder.species;
    this.career = builder.career;
    this.primarySpecialization = builder.primarySpecialization;
    this.secondarySpecializations.addAll(builder.secondarySpecializations);
    this.age = builder.age;
    this.height = builder.height;
    this.weight = builder.weight;
    this.skinTone = builder.skinTone;
    this.hairColor = builder.hairColor;
    this.eyeColor = builder.eyeColor;
    this.inventory.addAll(builder.inventory);
    this.wounds = builder.wounds;
    this.woundThreshold = builder.woundThreshold;
    this.strain = builder.strain;
    this.strainThreshold = builder.strainThreshold;
    this.meleeDefense = builder.meleeDefense;
    this.rangedDefense = builder.rangedDefense;
    this.soak = builder.soak;
    this.forceRating = builder.forceRating;
    this.forcePowers.putAll(builder.forcePowers);
    this.description = builder.description;
    this.accessTime = builder.accessTime;
    this.characterId = builder.characterId;
    this.lastOpenPage = builder.lastOpenPage;
    this.xp = builder.xp;
    this.wealth = builder.wealth;
    this.talents.putAll(builder.talents);
    this.actionConditions.putAll(builder.actionConditions);
    this.skillActions.putAll(builder.skillActions);
    this.attackActions.putAll(builder.attackActions);
    this.hiddenSections.addAll(builder.hiddenSections);
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

  public boolean isCareerSkill(@NotNull Skill skill) {
    if (career.getCareerSkills().contains(skill.getName())) {
      return true;
    }

    for (Specialization specialization : secondarySpecializations) {
      if (specialization.getCareerSkills().contains(skill.getName())) {
        return true;
      }
    }

    return false;
  }

  public void setSkillScore(@NotNull Skill skill, int value) {
    skills.put(skill, value);
  }

  public Map<Specialization, List<Integer>> getTalents() {
    if (!talents.entrySet().iterator().next().equals(primarySpecialization)) {
      return fixTalents();
    }

    if (secondarySpecializations.size() + 1 != talents.size()) {
      return fixTalents();
    }

    for (Specialization specialization : secondarySpecializations) {
      if (!talents.containsKey(specialization)) {
        return fixTalents();
      }
    }

    return talents;
  }

  private Map<Specialization, List<Integer>> fixTalents() {
    LinkedHashMap<Specialization, List<Integer>> newTalents = new LinkedHashMap<>();
    newTalents.put(primarySpecialization, getOrDefault(primarySpecialization, talents, new ArrayList<Integer>()));
    for (Specialization specialization : secondarySpecializations) {
      newTalents.put(specialization, getOrDefault(specialization, talents, new ArrayList<Integer>()));
    }

    talents.clear();
    talents.putAll(newTalents);
    return talents;
  }

  private <K, V> V getOrDefault(K key, Map<K, V> map, V defaultValue) {
    if (talents.containsKey(key)) {
      return map.get(key);
    }

    return defaultValue;
  }

  public void setPrimarySpecialization(@NotNull Specialization specialization) {
    talents.remove(primarySpecialization);
    primarySpecialization = specialization;
    talents.put(specialization, new ArrayList<Integer>());
  }

  public void addSecondarySpecialization(@NotNull Specialization specialization) {
    secondarySpecializations.add(specialization);
    talents.put(specialization, new ArrayList<Integer>());
  }

  public boolean removeSecondarySpecialization(@NotNull Specialization specialization) {
    talents.remove(specialization);
    return secondarySpecializations.remove(specialization);
  }

  @NotNull
  public List<RowData> getBasics() {
    List<RowData> rowData = new ArrayList<>();
    rowData.addAll(getIdentityBasics("Basics"));
    rowData.addAll(getCharacteristics("Basics"));
    rowData.addAll(getDefense("Basics"));
    return rowData;
  }

  @NotNull
  private List<RowData> getIdentityBasics(@NotNull String page) {
    List<RowData> rowData = new ArrayList<>();
    String sectionId = "Identity";
    if (!hiddenSections.contains(HiddenSection.of(page, sectionId))) {
      rowData.add(SectionRowData.of(sectionId, page, false));
      rowData.add(KeyValueRowData.of("Name", name, 0));
      rowData.add(KeyValueRowData.of("Species", species, 0));
      rowData.add(KeyValueRowData.of("Career", career.getName(), 0));
      String specializationLabel = "Specialization";
      if (secondarySpecializations.size() > 0) {
        specializationLabel = "Specializations";
      }

      rowData.add(KeyValueRowData.of(specializationLabel, primarySpecialization.getPrettyName(), 0));
      for (Specialization spec : secondarySpecializations) {
        rowData.add(KeyValueRowData.of("", spec.getLongPrettyName(), 0));
      }

      rowData.add(KeyValueRowData.of("XP", String.format(Locale.US, "%d", xp), 0));
    }
    else {
      rowData.add(SectionRowData.of(sectionId, page, true));
    }

    return rowData;
  }

  @NotNull
  private List<RowData> getCharacteristics(@NotNull String page) {
    List<RowData> rowData = new ArrayList<>();
    String sectionId = "Characteristics";
    if (!hiddenSections.contains(HiddenSection.of(page, sectionId))) {
      rowData.add(SectionRowData.of(sectionId, page, false));
      for (Characteristic ch : Characteristic.values()) {
        rowData.add(KeyValueRowData.of(ch.toString(), String.format(Locale.US, "%d", getCharacteristicScore(ch)), 0));
      }
    }
    else {
      rowData.add(SectionRowData.of(sectionId, page, true));
    }

    return rowData;
  }

  @NotNull
  private List<RowData> getDefense(@NotNull String page) {
    List<RowData> rowData = new ArrayList<>();
    String sectionId = "Defense";
    if (!hiddenSections.contains(HiddenSection.of(page, sectionId))) {
      rowData.add(SectionRowData.of(sectionId, page, false));
      rowData.add(ThresholdRowData.of("Wounds", String.format(Locale.US, " %d / %d", wounds, woundThreshold),
                                      ThresholdRowData.WOUNDS, 0));
      rowData.add(ThresholdRowData.of("Strain", String.format(Locale.US, " %d / %d", strain, strainThreshold),
                                      ThresholdRowData.STRAIN, 0));
      rowData.add(KeyValueRowData.of("Soak", String.format(Locale.US, "%d", soak), 0));
      rowData.add(KeyValueRowData.of("Defense (M/R)",
                                     String.format(Locale.US, "%d / %d", meleeDefense, rangedDefense), 0));
    }
    else {
      rowData.add(SectionRowData.of(sectionId, page, true));
    }

    return rowData;
  }

  @NotNull
  public List<RowData> getActions() {
    List<RowData> rowData = new ArrayList<>();
    rowData.addAll(getAttackActionsToShow("Action"));
    rowData.addAll(getSkillActionsToShow("Action"));
    rowData.addAll(getDefense("Action"));
    rowData.addAll(getActionConditionsToShow("Action"));
    return rowData;
  }

  @NotNull
  private List<RowData> getAttackActionsToShow(@NotNull String page) {
    List<RowData> rowData = new ArrayList<>();
    String sectionId = "Attacks";
    if (!hiddenSections.contains(HiddenSection.of(page, sectionId))) {
      rowData.add(SectionRowData.of(sectionId, page, false));
      for (Map.Entry<String, AttackAction> entry : attackActions.entrySet()) {
        rowData.add(AttackActionRowData.of(entry.getValue()));
      }
    }
    else {
      rowData.add(SectionRowData.of(sectionId, page, true));
    }

    return rowData;
  }

  @NotNull
  private List<RowData> getSkillActionsToShow(@NotNull String page) {
    List<RowData> rowData = new ArrayList<>();
    String sectionId = "Skills";
    if (!hiddenSections.contains(HiddenSection.of(page, sectionId))) {
      rowData.add(SectionRowData.of(sectionId, page, false));
      for (Map.Entry<String, SkillAction> entry : skillActions.entrySet()) {
        rowData.add(SkillActionRowData.of(entry.getValue()));
      }
    }
    else {
      rowData.add(SectionRowData.of(sectionId, page, true));
    }

    return rowData;
  }

  @NotNull
  private List<RowData> getActionConditionsToShow(@NotNull String page) {
    List<RowData> rowData = new ArrayList<>();
    String sectionId = "Conditions";
    if (!hiddenSections.contains(HiddenSection.of(page, sectionId))) {
      rowData.add(SectionRowData.of(sectionId, page, false));
      for (Map.Entry<String, Boolean> condition : actionConditions.entrySet()) {
        rowData.add(ToggleRowData.of(condition.getKey(), condition.getValue()));
      }
    }
    else {
      rowData.add(SectionRowData.of(sectionId, page, true));
    }

    return rowData;
  }

  public void setActionConditionState(@NotNull String actionCondition, boolean state) {
    actionConditions.put(actionCondition, state);
  }

  public boolean removeActionCondition(@NotNull String actionCondition) {
    return actionConditions.remove(actionCondition);
  }

  public void addSkillAction(@NotNull SkillAction skillAction) {
    skillActions.put(skillAction.getName(), skillAction);
  }

  public void addAttackAction(@NotNull AttackAction attackAction) {
    attackActions.put(attackAction.getName(), attackAction);
  }

  public boolean removeSkillAction(@NotNull String skillActionName) {
    return (skillActions.remove(skillActionName) != null);
  }

  public boolean removeAttackAction(@NotNull String attackActionName) {
    return (attackActions.remove(attackActionName) != null);
  }

  @NotNull
  public Set<String> getActiveConditions() {
    Set<String> activeConditions = new HashSet<>();
    activeConditions.add("Always Active");
    for (Map.Entry<String, Boolean> condition : actionConditions.entrySet()) {
      if (condition.getValue()) {
        activeConditions.add(condition.getKey());
      }
    }

    return activeConditions;
  }

  public List<String> getAvailableConditions(@NotNull String skillActionName) {
    Set<String> availableConditions = new LinkedHashSet<>(actionConditions.keySet());
    if (skillActions.containsKey(skillActionName)) {
      SkillAction skillAction = skillActions.get(skillActionName);
      for (String condition : skillAction.getConditionalBonuses().keySet()) {
        availableConditions.remove(condition);
      }
    }

    return Lists.newArrayList(availableConditions);
  }

  @NotNull
  public ImmutableMap<String, SkillAction> getAllSkillActions() {
    return ImmutableMap.copyOf(skillActions);
  }

  @NotNull
  public SkillAction getSkillAction(@NotNull String skillActionName) {
    if (skillActions.containsKey(skillActionName)) {
      return skillActions.get(skillActionName);
    }
    else {
      return SkillAction.of("", Characteristic.BRAWN, SkillManager.getGeneralSkills().get(0));
    }
  }

  @NotNull
  public AttackAction getAttackAction(@NotNull String attackActionName) {
    if (attackActions.containsKey(attackActionName)) {
      return attackActions.get(attackActionName);
    }
    else {
      return AttackAction.of("", Characteristic.BRAWN, SkillManager.getCombatSkills().get(0), Collections.EMPTY_MAP, 0,
                             0, AttackAction.Range.ENGAGED, "");
    }
  }

  @NotNull
  public Collection<String> getAllConditions() {
    List<String> conditions = new ArrayList<>(actionConditions.keySet());
    Collections.sort(conditions);
    return conditions;
  }

  public int getTotalWealth() {
    int totalWealth = 0;
    for (Integer amount : wealth.values()) {
      totalWealth += amount;
    }

    return totalWealth;
  }

  public void updateTimestamp() {
    accessTime = System.currentTimeMillis();
  }

  // Methods for writing to the JSON file.
  @NotNull
  public JSONObject toJsonObject() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(SPECIES_KEY, species);
    o.put(CAREER_KEY, career.getName());
    o.put(SPECIALIZATIONS_KEY, specializationsAsJsonObject());
    o.put(OBLIGATIONS_KEY, obligationsAsJsonArray());
    o.put(AGE_KEY, age);
    o.put(HEIGHT_KEY, height);
    o.put(WEIGHT_KEY, weight);
    o.put(SKIN_TONE_KEY, skinTone);
    o.put(HAIR_COLOR_KEY, hairColor);
    o.put(EYE_COLOR_KEY, eyeColor);
    o.put(INVENTORY_KEY, InventoryItem.toJsonArray(inventory));
    o.put(TALENTS_KEY, Talent.toJsonArray(talents));
    o.put(ACTION_CONDITIONS_KEY, actionConditionsAsJsonArray());
    o.put(ATTACK_ACTIONS_KEY, attackActionsAsJsonArray());
    o.put(SKILL_ACTIONS_KEY, skillActionsAsJsonArray());
    o.put(FORCE_POWERS_KEY, ForcePowerUpgrade.toJsonArray(forcePowers));
    o.put(HIDDEN_SECTIONS_KEY, hiddenSectionsAsJsonArray());
    o.put(DESCRIPTION_KEY, description);
    o.put(WOUNDS_KEY, wounds);
    o.put(WOUND_THRESHOLD_KEY, woundThreshold);
    o.put(STRAIN_KEY, strain);
    o.put(STRAIN_THRESHOLD_KEY, strainThreshold);
    o.put(MELEE_DEFENSE_KEY, meleeDefense);
    o.put(RANGED_DEFENSE_KEY, rangedDefense);
    o.put(SOAK_KEY, soak);
    o.put(FORCE_RATING_KEY, forceRating);
    o.put(CHARACTERISTICS_KEY, characteristicsAsJsonArray());
    o.put(SKILLS_KEY, skillsAsJsonArray());
    o.put(ID_KEY, characterId);
    o.put(LAST_OPEN_PAGE_KEY, lastOpenPage);
    o.put(XP_KEY, xp);
    o.put(WEALTH_KEY, wealthAsJsonArray());
    o.put(TIMESTAMP_KEY, accessTime);
    return o;
  }

  private JSONArray wealthAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (Map.Entry<String, Integer> entry : wealth.entrySet()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, entry.getKey());
      o.put(VALUE_KEY, entry.getValue());
      a.put(o);
    }

    return a;
  }

  @NotNull
  private JSONArray actionConditionsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (Map.Entry<String, Boolean> entry : actionConditions.entrySet()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, entry.getKey());
      o.put(SET_KEY, entry.getValue());
      a.put(o);
    }

    return a;
  }

  @NotNull
  private JSONArray skillActionsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (SkillAction skillAction : skillActions.values()) {
      a.put(skillAction.toJsonObject());
    }

    return a;
  }

  @NotNull
  private JSONArray attackActionsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (AttackAction attackAction : attackActions.values()) {
      a.put(attackAction.toJsonObject());
    }

    return a;
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

  private void putSkillsInJsonArray(@NotNull JSONArray a, @NotNull Map<Skill, Integer> skillMap) throws JSONException {
    for (Map.Entry<Skill, Integer> entry : skillMap.entrySet()) {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, entry.getKey().getName());
      o.put(SCORE_KEY, entry.getValue());
      a.put(o);
    }
  }

  @NotNull
  private JSONObject specializationsAsJsonObject() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(PRIMARY_KEY, primarySpecialization.getName());

    JSONArray otherSpecializations = new JSONArray();
    for (Specialization specialization : secondarySpecializations) {
      otherSpecializations.put(specialization.getName());
    }

    o.put(SECONDARY_KEY, otherSpecializations);
    return o;
  }

  @NotNull
  private JSONArray hiddenSectionsAsJsonArray() throws JSONException {
    JSONArray a = new JSONArray();
    for (HiddenSection hiddenSection : hiddenSections) {
      JSONObject o = new JSONObject();
      o.put(PAGE_KEY, hiddenSection.getPageName());
      o.put(SECTION_KEY, hiddenSection.getSectionName());
      a.put(o);
    }

    return a;
  }

  @NotNull
  private static Character valueOf(@NotNull Builder builder) {
    return new Character(builder);
  }

  // Methods for reading from the JSON file.
  @NotNull
  public static Character valueOf(@NotNull JSONObject jsonObject) throws IllegalArgumentException {
    String name = getJsonString(jsonObject, NAME_KEY);
    Career career = CareerManager.getCareer(getJsonString(jsonObject, CAREER_KEY));
    JSONObject specializations = getJsonObject(jsonObject, SPECIALIZATIONS_KEY);
    Specialization primarySpecialization = CareerManager.getSpecialization(getJsonString(specializations, PRIMARY_KEY));
    if (primarySpecialization == null) {
      throw new IllegalArgumentException("Invalid specialization: " + getJsonString(specializations, PRIMARY_KEY));
    }

    List<Specialization> secondarySpecializations = parseSpecializations(getJsonArray(specializations, SECONDARY_KEY));
    String species = getJsonString(jsonObject, SPECIES_KEY);
    Map<Skill, Integer> skills = parseSkills(getJsonArray(jsonObject, SKILLS_KEY));
    Map<Characteristic, Integer> characteristics = parseCharacteristics(getJsonArray(jsonObject, CHARACTERISTICS_KEY));
    UUID characterId = UUID.fromString(getJsonString(jsonObject, ID_KEY));

    // TODO Robustness on missing fields.
    Character character = new Builder(name, career, primarySpecialization, species, characterId)
                              .age(getJsonInt(jsonObject, AGE_KEY))
                              .height(getJsonString(jsonObject, HEIGHT_KEY))
                              .weight(getJsonString(jsonObject, WEIGHT_KEY))
                              .skinTone(getJsonString(jsonObject, SKIN_TONE_KEY))
                              .hairColor(getJsonString(jsonObject, HAIR_COLOR_KEY))
                              .eyeColor(getJsonString(jsonObject, EYE_COLOR_KEY))
                              .inventory(InventoryItem.parseInventory(getJsonArray(jsonObject, INVENTORY_KEY)))
                              .wounds(getJsonInt(jsonObject, WOUNDS_KEY))
                              .woundThreshold(getJsonInt(jsonObject, WOUND_THRESHOLD_KEY))
                              .strain(getJsonInt(jsonObject, STRAIN_KEY))
                              .strainThreshold(getJsonInt(jsonObject, STRAIN_THRESHOLD_KEY))
                              .meleeDefense(getJsonInt(jsonObject, MELEE_DEFENSE_KEY))
                              .rangedDefense(getJsonInt(jsonObject, RANGED_DEFENSE_KEY))
                              .soak(getJsonInt(jsonObject, SOAK_KEY))
                              .talents(Talent.parseJsonArray(getJsonArray(jsonObject, TALENTS_KEY)))
                              .forcePowers(ForcePowerUpgrade.parseJsonArray(getJsonArray(jsonObject, FORCE_POWERS_KEY)))
                              .description(getJsonString(jsonObject, DESCRIPTION_KEY))
                              .forceRating(getJsonInt(jsonObject, FORCE_RATING_KEY))
                              .accessTime(getJsonLong(jsonObject, TIMESTAMP_KEY))
                              .lastOpenPage(getJsonInt(jsonObject, LAST_OPEN_PAGE_KEY))
                              .xp(getJsonInt(jsonObject, XP_KEY))
                              .wealth(parseWealth(getJsonArray(jsonObject, WEALTH_KEY)))
                              .actionConditions(parseActionConditions(getJsonArray(jsonObject, ACTION_CONDITIONS_KEY)))
                              .skillActions(parseSkillActions(getJsonArray(jsonObject, SKILL_ACTIONS_KEY)))
                              .attackActions(parseAttackActions(getJsonArray(jsonObject, ATTACK_ACTIONS_KEY)))
                              .hiddenSections(parseHiddenSections(getJsonArray(jsonObject, HIDDEN_SECTIONS_KEY)))
                              .secondarySpecializations(secondarySpecializations)
                              .build();
    for (Map.Entry<Skill, Integer> skill : skills.entrySet()) {
      character.setSkillScore(skill.getKey(), skill.getValue());
    }

    for (Map.Entry<Characteristic, Integer> characteristic : characteristics.entrySet()) {
      character.setCharacteristicScore(characteristic.getKey(), characteristic.getValue());
    }

    // TODO:
    // Obligation

    return character;
  }

  @NotNull
  private static JSONObject getJsonObject(@NotNull JSONObject jsonObject, @NotNull String key) {
    try {
      return jsonObject.getJSONObject(key);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading value for " + key, e);
      return new JSONObject();
    }
  }

  @NotNull
  private static JSONArray getJsonArray(@NotNull JSONObject jsonObject, @NotNull String key) {
    try {
      return jsonObject.getJSONArray(key);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading value for " + key, e);
      return new JSONArray();
    }
  }

  private static long getJsonLong(@NotNull JSONObject jsonObject, @NotNull String key) {
    try {
      return jsonObject.getLong(key);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading value for " + key, e);
      return 0;
    }
  }

  private static int getJsonInt(@NotNull JSONObject jsonObject, @NotNull String key) {
    try {
      return jsonObject.getInt(key);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading value for " + key, e);
      return 0;
    }
  }

  @NotNull
  private static String getJsonString(@NotNull JSONObject jsonObject, @NotNull String key) {
    try {
      return jsonObject.getString(key);
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading value for " + key, e);
      return "";
    }
  }

  @NotNull
  private static Map<Characteristic, Integer> parseCharacteristics(@NotNull JSONArray jsonArray) {
    Map<Characteristic, Integer> characteristics = new HashMap<>();
    for (Characteristic c : Characteristic.values()) {
      characteristics.put(c, 2);
    }

    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject characteristicJson = jsonArray.getJSONObject(i);
        Characteristic characteristic = Characteristic.of(characteristicJson.getString(NAME_KEY));
        characteristics.put(characteristic, characteristicJson.getInt(SCORE_KEY));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading characteristic object at index %d.", i), e);
      }
    }

    return characteristics;
  }

  @NotNull
  private static Map<Skill, Integer> parseSkills(@NotNull JSONArray jsonArray) {
    Map<Skill, Integer> skills = new HashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject skillJson = jsonArray.getJSONObject(i);
        Skill skill = SkillManager.getSkill(skillJson.getString(NAME_KEY));
        skills.put(skill, skillJson.getInt(SCORE_KEY));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading skill object at index %d.", i), e);
      }
    }

    return skills;
  }

  @NotNull
  private static List<Specialization> parseSpecializations(@NotNull JSONArray jsonArray) {
    List<Specialization> specializations = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        specializations.add(CareerManager.getSpecialization(jsonArray.getString(i)));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading secondarySpecialization at index %d.", i), e);
      }
    }

    return specializations;
  }

  @NotNull
  private static Map<String, Integer> parseWealth(@NotNull JSONArray jsonArray) {
    Map<String, Integer> wealth = new LinkedHashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject o = jsonArray.getJSONObject(i);
        wealth.put(o.getString(NAME_KEY), o.getInt(VALUE_KEY));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading wealth entry at index %d.", i), e);
      }
    }

    return wealth;
  }

  @NotNull
  private static Map<String, Boolean> parseActionConditions(@NotNull JSONArray jsonArray) {
    Map<String, Boolean> actionConditions = new HashMap<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject o = jsonArray.getJSONObject(i);
        actionConditions.put(o.getString(NAME_KEY), o.getBoolean(SET_KEY));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading action condition at index %d.", i), e);
      }
    }

    return actionConditions;
  }

  @NotNull
  private static List<SkillAction> parseSkillActions(@NotNull JSONArray jsonArray) {
    List<SkillAction> skillActions = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        skillActions.add(SkillAction.of(jsonArray.getJSONObject(i)));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading skill action at index %d.", i), e);
      }

    }

    return skillActions;
  }

  @NotNull
  private static List<AttackAction> parseAttackActions(@NotNull JSONArray jsonArray) {
    List<AttackAction> attackActions = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        attackActions.add(AttackAction.of(jsonArray.getJSONObject(i)));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading skill action at index %d.", i), e);
      }

    }

    return attackActions;
  }

  @NotNull
  private static List<HiddenSection> parseHiddenSections(@NotNull JSONArray jsonArray) {
    List<HiddenSection> hiddenSections = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        hiddenSections.add(HiddenSection.of(jsonArray.getJSONObject(i)));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format(Locale.US, "Error reading hidden section at index %d.", i), e);
      }
    }

    return hiddenSections;
  }

  public Summary makeSummary() {
    return new Summary(characterId, name, species, career.getName(), accessTime);
  }

  public static String buildFileName(@NotNull UUID characterId) {
    return characterId.toString() + ".json";
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
    int threshold = characteristicScores.get(Characteristic.BRAWN) + 5;
    for (InventoryItem item : inventory) {
      if (item.isCountEncumbrance() && item.getEncumbrance() < 0) {
        threshold -= item.getEncumbrance() * item.getQuantity();
      }
    }

    return threshold;
  }

  public void setWounds(int i) {
    wounds = Math.max(i, 0);
  }

  public void setStrain(int i) {
    strain = Math.max(i, 0);
  }

  @NotNull
  public String getFileName() {
    return name.replace(' ', '_') + ".json";
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

    @NotNull
    public static Summary valueOf(@NotNull JSONObject characterJson) throws JSONException {
      String name = characterJson.getString(NAME_KEY);
      Career career = CareerManager.getCareer(characterJson.getString(CAREER_KEY));
      String species = characterJson.getString(SPECIES_KEY);
      long timestamp = characterJson.getLong(TIMESTAMP_KEY);
      UUID characterId = UUID.fromString(characterJson.getString(ID_KEY));

      return new Summary(characterId, name, species, career.getName(), timestamp);
    }

    @NotNull
    public String getBlurb() {
      return species + " - " + career;
    }

    @NotNull
    public String getTimestampString() {
      return new Date(timestamp).toString();
    }

    @NotNull
    public JSONObject toJson() throws JSONException {
      JSONObject o = new JSONObject();
      o.put(NAME_KEY, name);
      o.put(CAREER_KEY, career);
      o.put(SPECIES_KEY, species);
      o.put(TIMESTAMP_KEY, timestamp);
      o.put(ID_KEY, characterId.toString());
      return o;
    }
  }

  private static class Builder {
    private final String name;
    private int age = 0;
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
    private int forceRating = 0;

    private long accessTime;
    private int xp = 0;
    private int lastOpenPage = 0;
    private List<InventoryItem> inventory = new ArrayList<>();
    private Map<String, Boolean> actionConditions = new HashMap<>();
    private Map<String, SkillAction> skillActions = new LinkedHashMap<>();
    private Map<String, AttackAction> attackActions = new LinkedHashMap<>();
    private List<HiddenSection> hiddenSections = new ArrayList<>();
    private Map<String, Integer> wealth = new LinkedHashMap<>();

    private final Career career;
    private final Specialization primarySpecialization;
    private final String species;
    private final UUID characterId;
    private List<Specialization> secondarySpecializations = new ArrayList<>();
    private Map<Specialization, List<Integer>> talents;
    private Map<String, List<Integer>> forcePowers;

    private String description;

    Builder(@NotNull String name, @NotNull Career career, @NotNull Specialization specialization,
            @NotNull String species, @NotNull UUID characterId) {
      this.name = name;
      this.career = career;
      this.primarySpecialization = specialization;
      this.species = species;
      this.accessTime = System.currentTimeMillis();
      this.characterId = characterId;
    }

    @NotNull
    Builder secondarySpecializations(List<Specialization> secondarySpecializations) {
      for (Specialization specialization : secondarySpecializations) {
        if (!specialization.equals(primarySpecialization)) {
          this.secondarySpecializations.add(specialization);
        }
      }

      return this;
    }

    @NotNull
    Builder accessTime(long accessTime) {
      this.accessTime = accessTime;
      return this;
    }

    @NotNull
    Builder age(int age) {
      this.age = age;
      return this;
    }

    @NotNull
    Builder height(@NotNull String height) {
      this.height = height;
      return this;
    }

    @NotNull
    Builder weight(@NotNull String weight) {
      this.weight = weight;
      return this;
    }

    @NotNull
    Builder skinTone(@NotNull String skinTone) {
      this.skinTone = skinTone;
      return this;
    }

    @NotNull
    Builder hairColor(@NotNull String hairColor) {
      this.hairColor = hairColor;
      return this;
    }

    @NotNull
    Builder eyeColor(@NotNull String eyeColor) {
      this.eyeColor = eyeColor;
      return this;
    }

    @NotNull
    Builder wounds(int wounds) {
      this.wounds = wounds;
      return this;
    }

    @NotNull
    Builder woundThreshold(int woundThreshold) {
      this.woundThreshold = woundThreshold;
      return this;
    }

    @NotNull
    Builder strain(int strain) {
      this.strain = strain;
      return this;
    }

    @NotNull
    Builder strainThreshold(int strainThreshold) {
      this.strainThreshold = strainThreshold;
      return this;
    }

    @NotNull
    Builder meleeDefense(int meleeDefense) {
      this.meleeDefense = meleeDefense;
      return this;
    }

    @NotNull
    Builder rangedDefense(int rangedDefense) {
      this.rangedDefense = rangedDefense;
      return this;
    }

    @NotNull
    Builder soak(int soak) {
      this.soak = soak;
      return this;
    }

    @NotNull
    Builder forceRating(int forceRating) {
      this.forceRating = forceRating;
      return this;
    }

    @NotNull
    Builder wealth(@NotNull Map<String, Integer> wealth) {
      this.wealth.putAll(wealth);
      return this;
    }

    @NotNull
    Builder actionConditions(@NotNull Map<String, Boolean> actionConditions) {
      this.actionConditions.putAll(actionConditions);
      return this;
    }

    @NotNull
    Builder skillActions(@NotNull List<SkillAction> skillActions) {
      for (SkillAction skillAction : skillActions) {
        this.skillActions.put(skillAction.getName(), skillAction);
      }

      return this;
    }

    @NotNull
    Builder attackActions(@NotNull List<AttackAction> attackActions) {
      for (AttackAction attackAction : attackActions) {
        this.attackActions.put(attackAction.getName(), attackAction);
      }

      return this;
    }

    @NotNull
    Builder hiddenSections(@NotNull List<HiddenSection> hiddenSections) {

      this.hiddenSections.addAll(hiddenSections);
      return this;
    }

    @NotNull
    Builder xp(int xp) {
      this.xp = xp;
      return this;
    }

    @NotNull
    Builder lastOpenPage(int lastOpenPage) {
      this.lastOpenPage = lastOpenPage;
      return this;
    }

    @NotNull
    Character build() {
      return Character.valueOf(this);
    }

    Builder inventory(@NotNull List<InventoryItem> inventory) {
      this.inventory = inventory;
      return this;
    }

    Builder talents(@NotNull Map<Specialization, List<Integer>> talents) {
      this.talents = talents;
      return this;
    }

    Builder forcePowers(@NotNull Map<String, List<Integer>> forcePowers) {
      this.forcePowers = forcePowers;
      return this;
    }

    Builder description(@NotNull String description) {
      this.description = description;
      return this;
    }
  }

  public void toggleSection(@NotNull String pageName, @NotNull String sectionName) {
    HiddenSection hiddenSection = HiddenSection.of(pageName, sectionName);
    if (hiddenSections.contains(hiddenSection)) {
      hiddenSections.remove(hiddenSection);
    }
    else {
      hiddenSections.add(hiddenSection);
    }
  }

  public boolean isSectionHidden(@NotNull String pageName, @NotNull String sectionName) {
    return hiddenSections.contains(HiddenSection.of(pageName, sectionName));
  }

  @Getter
  @EqualsAndHashCode
  @RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
  private static class HiddenSection {
    private final String pageName;
    private final String sectionName;

    public static HiddenSection of(@NotNull JSONObject jsonObject) throws JSONException {
      String pageName = jsonObject.getString(PAGE_KEY);
      String sectionName = jsonObject.getString(SECTION_KEY);
      return HiddenSection.of(pageName, sectionName);
    }
  }

  private static class MostRecentAccessComparator implements Comparator<Character.Summary> {
    @Override
    public int compare(Character.Summary lhs, Character.Summary rhs) {
      return (int) (rhs.timestamp - lhs.timestamp);
    }
  }
}
