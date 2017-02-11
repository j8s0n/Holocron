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

public final class FileAccessor {
  private static File GameData = new File(Environment.getExternalStorageDirectory(), "GameData");
  private static File Holocron = new File(GameData, "Holocron");
  private static File Rules = new File(Holocron, "Rules");
  private static File Talents = new File(Rules, "Talents");
  private static File ForcePowers = new File(Rules, "ForcePowers");
  private static File Characters = new File(Holocron, "Characters");
  private static final String LOG_TAG = FileAccessor.class.getSimpleName();

  static {
    if ((!Rules.exists() && !Rules.mkdirs()) ||
        (!Characters.exists() && !Characters.mkdirs())) {
      Log.e(LOG_TAG, "Unable to create directories.");
    }
  }

  private FileAccessor() {
  }

  @NotNull
  public static Map<UUID, String> getAllCharacterContent() {
    Map<UUID, String> characters = new HashMap<>();
    for (File f : Characters.listFiles()) {
      String fileName = f.getName();
      int index = fileName.indexOf('.') + 1;
      UUID id = UUID.fromString(fileName.substring(index));
      characters.put(id, readFile(f));
    }

    return characters;
  }

  @NotNull
  public static String getCharacterContent(String characterFileName) {
    return readFile(new File(Characters, characterFileName));
  }

  public static void writeCharacterContent(@NotNull Character character) throws JSONException {
    File characterFile = new File(Characters, character.getFileName());
    writeFile(characterFile, character.toJsonObject().toString(2));
  }

  @NotNull
  public static String getSkillContent() {
    File skills = new File(Rules, "Skills.json");
    return readFile(skills);
  }

  @NotNull
  public static Map<String, String> getTalentsContent() {
    Map<String, String>  talentMap = new HashMap<>();
    for (File f : Talents.listFiles()) {
      String fileName = f.getName();
      int index = fileName.indexOf(".json");
      if (index > 0) {
        String talent = fileName.substring(0, index);
        talentMap.put(talent, readFile(f));
      }
    }

    return talentMap;
  }

  @NotNull
  public static String getCareerContent() {
    File careers = new File(Rules, "Careers.json");
    return readFile(careers);
  }

  @NotNull
  public static String getWeaponContent() {
    File weapons = new File(Rules, "Weapons.json");
    return readFile(weapons);
  }

  @NotNull
  public static String getArmorContent() {
    File armor = new File(Rules, "Armor.json");
    return readFile(armor);
  }

  @NotNull
  public static String getGearContent() {
    File gear = new File(Rules, "Gear.json");
    return readFile(gear);
  }

  @NotNull
  private static String readFile(@NotNull File file) {
    int retryCount = 0;
    while (retryCount < 5) {
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
        if (retryCount >= 5) {
          throw new IllegalStateException("Unable to read file: " + file.getAbsoluteFile(), e);
        }
      }
      catch (IOException e) {
        throw new IllegalStateException("Unable to read file: " + file.getAbsoluteFile(), e);
      }

      retryCount++;
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return "";
  }

  private static void writeFile(@NotNull File file, @NotNull String data) {
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

  public static void removeFile(@NotNull String characterId) {
    File removeIt = new File(Characters, characterId);
    boolean success = removeIt.delete();
    Log.i(LOG_TAG, "Remove " + characterId + ": " + success);
  }
}
