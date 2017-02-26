package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Character.Summary;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class CharacterManager extends ManagerBase {
  private static final String LOG_TAG = CharacterManager.class.getSimpleName();
  private static final String CHARACTERS_FILE = "Characters.json";

  private static final Map<UUID, Summary> characters = new LinkedHashMap<>();
  private static Character activeCharacter;

  private CharacterManager() {
  }

  @NotNull
  public static Character getActiveCharacter() {
    if (activeCharacter == null) {
      throw new IllegalStateException("There is no valid character. How did we get here?");
    }

    return activeCharacter;
  }

  public static void saveCharacter(@NotNull Context context, @NotNull Character character) {
    characters.put(character.getCharacterId(), character.makeSummary());
    character.updateTimestamp();
    characters.put(character.getCharacterId(), character.makeSummary());
    writeSummaries(context);
    writeCharacter(context, character);
  }

  public static void loadCharacters(@NotNull Context context) {
    getFileContent(context, CHARACTERS_FILE, true, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          characters.clear();
          JSONArray charactersJson = new JSONArray(content);
          for (int i = 0; i < charactersJson.length(); i++) {
            JSONObject character = charactersJson.getJSONObject(i);
            Summary summary = Summary.valueOf(character);
            characters.put(summary.getCharacterId(), summary);
          }
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Error reading Characters.json", e);
        }

      }
    });
  }

  private static void loadSingleCharacter(@NotNull Context context, @NotNull final UUID characterId) {
    String fileName = characterId.toString() + ".json";
    getFileContent(context, fileName, false, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          CharacterManager.setActiveCharacter(Character.valueOf(new JSONObject(content), characterId));
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Error reading Character: " + characterId, e);
        }
      }
    });
  }

  private static void writeSummaries(@NotNull final Context context) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          JSONArray summaries = new JSONArray();
          for (Summary summary : characters.values()) {
            summaries.put(summary.toJson());
          }

          FileOutputStream fos = context.openFileOutput(CHARACTERS_FILE, Context.MODE_PRIVATE);
          fos.write(summaries.toString().getBytes());
          fos.close();
        }
        catch (IOException | JSONException e) {
          Log.e(LOG_TAG, "Error writing character summaries.", e);
        }
      }
    });
  }

  private static void writeCharacter(@NotNull final Context context, @NotNull final Character character) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          FileOutputStream fos = context.openFileOutput(Character.buildFileName(character.getCharacterId()),
                                                        Context.MODE_PRIVATE);
          fos.write(character.toJsonObject().toString().getBytes());
          fos.close();
        }
        catch (JSONException | IOException e) {
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

  public static void removeCharacter(@NotNull Summary summary, @NotNull Context context) {
    characters.remove(summary.getCharacterId());
    writeSummaries(context);
    String fileName = Character.buildFileName(summary.getCharacterId());
    context.deleteFile(fileName);
  }

  public static void setActiveCharacter(@NotNull Character character) {
    activeCharacter = character;
  }

  public static void loadCharacterToActiveState(@NotNull Context context, @NotNull UUID characterId) {
    if (activeCharacter == null || !activeCharacter.getCharacterId().equals(characterId)) {
      loadSingleCharacter(context, characterId);
    }
  }

  public static void saveActiveCharacter(@NotNull Context context) {
    if (activeCharacter != null) {
      saveCharacter(context, activeCharacter);
    }
  }
}
