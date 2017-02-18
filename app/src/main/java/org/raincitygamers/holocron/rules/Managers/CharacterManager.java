package org.raincitygamers.holocron.rules.managers;

import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Character.Summary;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class CharacterManager {
  private static final String LOG_TAG = CharacterManager.class.getSimpleName();
  private static final Map<UUID, Summary> characters = new LinkedHashMap<>();
  private static Character activeCharacter;

  static {
    loadCharacters();
  }

  private CharacterManager() {
  }

  @Nullable
  public static Character getActiveCharacter() {
    return activeCharacter;
  }

  public static void saveCharacter(@NotNull Character character) {
    characters.put(character.getCharacterId(), character.makeSummary());
    character.updateTimestamp();
    writeCharacter(character);
  }

  private static void loadCharacters() {
    characters.clear();
    for (Map.Entry<UUID, String> entry : FileAccessor.getAllCharacterContent().entrySet()) {
      try {
        JSONObject characterJson = new JSONObject(entry.getValue());
        characters.put(entry.getKey(), Summary.valueOf(characterJson, entry.getKey()));
      }
      catch (JSONException | NullPointerException e) {
        Log.e(LOG_TAG, "Error reading character file: " + entry.getKey(), e);
      }
    }
  }

  private static void writeCharacter(@NotNull final Character character) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          FileAccessor.writeCharacterContent(character);
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Error writing character: '" + character.getName() + "' " + character.getCharacterId(), e);
        }
      }
    });
  }

  @Nullable
  public static Summary getCharacterSummary(@NotNull UUID characterId) {
    return characters.get(characterId);
  }

  public static Collection<UUID> getCharacterIds() {
    return characters.keySet();
  }

  public static void removeCharacter(@NotNull Summary summary) {
    characters.remove(summary.getCharacterId());
    FileAccessor.removeFile(summary.getFileName());
  }

  public static void clearActiveCharacter() {
    activeCharacter = null;
  }

  public static void setActiveCharacter(@NotNull Character character) {
    activeCharacter = character;
  }

  public static void loadActiveCharacter(@NotNull String characterFileName, @NotNull UUID characterId) {
    try {
      if (activeCharacter == null || !activeCharacter.getCharacterId().equals(characterId)) {
        activeCharacter = Character.valueOf(new JSONObject(FileAccessor.getCharacterContent(characterFileName)),
                                            characterId);
      }
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading character file: " + characterId, e);
      throw new IllegalStateException("No valid character to display.", e);
    }
  }

  public static void saveActiveCharacter() {
    if (activeCharacter != null) {
      saveCharacter(activeCharacter);
    }
  }
}
