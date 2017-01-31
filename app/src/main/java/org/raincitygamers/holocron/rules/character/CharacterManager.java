package org.raincitygamers.holocron.rules.character;

import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.character.Character.Summary;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CharacterManager {
  private static final String LOG_TAG = CharacterManager.class.getSimpleName();
  private static final CharacterManager ourInstance = new CharacterManager();
  private final Map<UUID, Summary> characters = new LinkedHashMap<>();
  private final FileAccessor fileAccessor = FileAccessor.getInstance();
  private Character activeCharacter;

  public static CharacterManager getInstance() {
    return ourInstance;
  }

  private CharacterManager() {
    loadCharacters();
  }

  @Nullable
  public static Character getActiveCharacter() {
    return getInstance().activeCharacter;
  }

  public void saveCharacter(@NotNull Character character) {
    characters.put(character.getCharacterId(), character.makeSummary());
    character.updateTimestamp();
    writeCharacter(character);
  }

  private void loadCharacters() {
    characters.clear();
    for (Map.Entry<UUID, String> entry : fileAccessor.getAllCharacterContent().entrySet()) {
      try {
        JSONObject characterJson = new JSONObject(entry.getValue());
        characters.put(entry.getKey(), Summary.valueOf(characterJson, entry.getKey()));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, "Error reading character file: " + entry.getKey(), e);
      }
      catch (NullPointerException e) {
        Log.e(LOG_TAG, "Error reading character file: " + entry.getKey(), e);
      }
    }
  }

  private void writeCharacter(@NotNull final Character character) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          fileAccessor.writeCharacterContent(character);
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Error writing character: '" + character.getName() + "' " + character.getCharacterId(), e);
        }
      }
    });
  }

  @Nullable
  public Summary getCharacterSummary(@NotNull UUID characterId) {
    return characters.get(characterId);
  }

  public Collection<UUID> getCharacterIds() {
    return characters.keySet();
  }

  public void removeCharacter(@NotNull Summary summary) {
    characters.remove(summary.getCharacterId());
    fileAccessor.removeFile(summary.getFileName());
  }

  public void clearActiveCharacter() {
    activeCharacter = null;
  }

  public void loadActiveCharacter(@NotNull String characterFileName, @NotNull UUID characterId) {
    try {
      if (activeCharacter == null || !activeCharacter.getCharacterId().equals(characterId)) {
        activeCharacter = Character.valueOf(new JSONObject(fileAccessor.getCharacterContent(characterFileName)),
                                            characterId);
      }
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading character file: " + characterId, e);
      throw new IllegalStateException("No valid character to display.", e);
    }
  }
}
