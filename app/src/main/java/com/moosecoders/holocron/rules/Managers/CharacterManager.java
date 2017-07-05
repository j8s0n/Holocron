package com.moosecoders.holocron.rules.managers;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.character.Character.Summary;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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

  public static Character getActiveCharacter() {
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
    getFileContent(context, CHARACTERS_FILE, FileSource.LOCAL_ONLY, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          characters.clear();
          JSONArray charactersJson = content.isEmpty() ? new JSONArray() : new JSONArray(content);
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
    String fileName = Character.buildFileName(characterId);
    getFileContent(context, fileName, FileSource.LOCAL_ONLY, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        try {
          setActiveCharacter(Character.valueOf(new JSONObject(content)));
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Error reading Character: " + characterId, e);
        }
      }
    });
  }

  public static void loadCharacterFromContent(@NotNull final Uri uri, @NotNull final AppCompatActivity activity,
                                              @NotNull final Runnable finisher) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          InputStream is = activity.getContentResolver().openInputStream(uri);
          if (is != null) {
            String content = readInputStream(is);
            loadCharacterFromText(content, activity, finisher);
          }
        }
        catch (IOException e) {
          Log.e(LOG_TAG, "Error reading character from content URI.", e);
        }
      }
    });
  }

  public static void loadCharacterFromText(@NotNull final String content, @NotNull final AppCompatActivity activity,
                                           @NotNull final Runnable finisher) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          setActiveCharacter(Character.valueOf(new JSONObject(content)));
          saveActiveCharacter(activity);
          activity.runOnUiThread(finisher);
        }
        catch (JSONException e) {
          Log.e(LOG_TAG, "Error reading character from content string.", e);
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

          writeStringToFile(context, CHARACTERS_FILE, summaries.toString(2));
        }
        catch (JSONException e) {
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
          writeStringToFile(context, Character.buildFileName(character.getCharacterId()),
                            character.toJsonObject().toString(2));
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
