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
  public static Map<String, String> getForcePowersContent() {
    Map<String, String>  forcePowersMap = new HashMap<>();
    for (File f : ForcePowers.listFiles()) {
      String fileName = f.getName();
      int index = fileName.indexOf(".json");
      if (index > 0) {
        String powerName = fileName.substring(0, index).replace('_', ' ');
        forcePowersMap.put(powerName, readFile(f));
      }
    }

    return forcePowersMap;
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
