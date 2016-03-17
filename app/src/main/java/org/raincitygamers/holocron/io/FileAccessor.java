package org.raincitygamers.holocron.io;


import android.os.Environment;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.raincitygamers.holocron.rules.character.Character;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileAccessor {
  private File Base = new File(Environment.getExternalStorageDirectory(), ".holocron");
  private File Rules = new File(Base, "Rules");
  private File Characters = new File(Base, "Characters");
  private static final String LOG_TAG = FileAccessor.class.getSimpleName();
  private static FileAccessor ourInstance = new FileAccessor();

  @NotNull
  public static FileAccessor getInstance() {
    return ourInstance;
  }

  private FileAccessor() {
    if ((!Rules.exists() && !Rules.mkdirs()) ||
        (!Characters.exists() && !Characters.mkdirs())) {
      Log.e(LOG_TAG, "Unable to create directories.");
    }
  }

  @NotNull
  public Map<UUID, String> getCharacterContent() {
    Map<UUID, String> characters = new HashMap<>();
    for (File f : Characters.listFiles()) {
      characters.put(UUID.fromString(f.getName()), readFile(f));
    }

    return characters;
  }

  public void writeCharacterContent(@NotNull Character character) throws JSONException {
    File characterFile = new File(Characters, character.getCharacterId().toString());
    writeFile(characterFile, character.toJsonObject().toString());
  }

  @NotNull
  public String getSkillContent() {
    File skills = new File(Rules, "Skills.json");
    return readFile(skills);
  }

  @NotNull
  public String getTalentContent() {
    File talents = new File(Rules, "Talents.json");
    return readFile(talents);
  }

  @NotNull
  public String getSpeciesContent() {
    File species = new File(Rules, "Species.json");
    return readFile(species);
  }

  @NotNull
  public String getCareerContent() {
    File careers = new File(Rules, "Careers.json");
    return readFile(careers);
  }

  @NotNull
  public String getWeaponContent() {
    File weapons = new File(Rules, "Weapons.json");
    return readFile(weapons);
  }

  @NotNull
  public String getArmorContent() {
    File armor = new File(Rules, "Armor.json");
    return readFile(armor);
  }

  @NotNull

  public String getGearContent() {
    File gear = new File(Rules, "Gear.json");
    return readFile(gear);
  }

  @NotNull
  private String readFile(@NotNull File file) {
    try (InputStream is = new FileInputStream(file)) {
      byte[] buffer = new byte[65536];
      StringBuilder sb = new StringBuilder();
      int count;
      while ((count = is.read(buffer)) >= 0) {
        sb.append(new String(buffer, 0, count));
      }

      return sb.toString();
    }
    catch (FileNotFoundException e) {
      throw new IllegalStateException("Unable to read file: " + file.getAbsoluteFile(), e);
    }
    catch (IOException e) {
      throw new IllegalStateException("Unable to read file: " + file.getAbsoluteFile(), e);
    }
  }

  private void writeFile(@NotNull File file, @NotNull String data) {
    try (OutputStream os = new FileOutputStream(file)) {
      if (!file.exists()) {
        file.createNewFile();
      }

      os.write(data.getBytes());
    }
    catch (FileNotFoundException e) {
      throw new IllegalStateException("Unable to write file: " + file.getAbsoluteFile(), e);
    }
    catch (IOException e) {
      throw new IllegalStateException("Unable to write file: " + file.getAbsoluteFile(), e);
    }
  }

  public void removeFile(@NotNull UUID characterId) {
    File removeIt = new File(Characters, characterId.toString());
    boolean success = removeIt.delete();
    Log.i(LOG_TAG, "Remove " + characterId + ": " + success);
  }
}
