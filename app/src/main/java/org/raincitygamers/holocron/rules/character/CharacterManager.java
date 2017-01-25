package org.raincitygamers.holocron.rules.character;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.io.FileAccessor;
import org.raincitygamers.holocron.rules.character.Character.Summary;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

public class CharacterManager {
  private static final String LOG_TAG = CharacterManager.class.getSimpleName();
  private static final CharacterManager ourInstance = new CharacterManager();
  private final Map<UUID, Summary> characters = new LinkedHashMap<>();
  private final FileAccessor fileAccessor = FileAccessor.getInstance();
  @Getter private Character activeCharacter;

  public static CharacterManager getInstance() {
    return ourInstance;
  }

  private CharacterManager() {
    loadCharacters();
  }

  @NotNull
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

  public void removeCharacter(@NotNull Summary character) {
    characters.remove(character.getCharacterId());
    fileAccessor.removeFile(character.getCharacterId());
  }

  public void loadActiveCharacter(UUID characterId) {
    if (characterId == null) {
      activeCharacter = null;
      return;
    }

    try {
      if (activeCharacter == null || !activeCharacter.getCharacterId().equals(characterId)) {
        activeCharacter = Character.valueOf(new JSONObject(fileAccessor.getCharacterContent(characterId)), characterId);
      }
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error reading character file: " + characterId, e);
      throw new IllegalStateException("No valid character to display.", e);
    }
  }
}
