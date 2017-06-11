package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DownloadManager extends ManagerBase {
  private static final String VERSIONS_FILE = "Versions.json";
  private static final String NAME_KEY = "name";
  private static final String VERSION_KEY = "version";
  private static final String LOG_TAG = DownloadManager.class.getSimpleName();

  private static final Map<String, Integer> fileVersions = new HashMap<>();

  static {
    fileVersions.put(TalentManager.TALENTS_FILE, 0);
    fileVersions.put(CareerManager.CAREERS_FILE, 0);
    fileVersions.put(ForcePowerManager.FORCE_POWERS_FILE, 0);
    fileVersions.put(SkillManager.SKILLS_FILE, 0);
  }

  public static void checkForUpdates(@NotNull final Context context) {
    getFileContent(context, VERSIONS_FILE, FileSource.LOCAL_ONLY, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        // Check the local version of the file (if present) against the remote version.
        checkVersions(context, content);
      }
    });
  }

  private static void checkVersions(@NotNull final Context context, @NotNull String content) {
    if (!content.isEmpty()) {
      parseVersionInfo(content, fileVersions);
    }

    final Map<String, Integer> availableVersions = new HashMap<>();
    getFileContent(context, VERSIONS_FILE, FileSource.DOWNLOAD_ONLY, new ContentParser() {
      @Override
      public void parse(@NotNull String content) {
        parseVersionInfo(content, availableVersions);
        updateAsNeeded(context, availableVersions);
      }
    });
  }

  private static void updateAsNeeded(@NotNull final Context context, @NotNull Map<String, Integer> availableVersions) {
    for (final String fileName : fileVersions.keySet()) {
      if (availableVersions.get(fileName) > fileVersions.get(fileName)) {
        fileVersions.put(fileName, availableVersions.get(fileName));

        getFileContent(context, fileName, FileSource.DOWNLOAD_ONLY, new ContentParser() {
          @Override
          public void parse(@NotNull String content) {
            switch (fileName) {
            case SkillManager.SKILLS_FILE:
              writeStringToFile(context, SkillManager.SKILLS_FILE, content);
              SkillManager.loadSkills(context);
              break;
            case TalentManager.TALENTS_FILE:
              writeStringToFile(context, TalentManager.TALENTS_FILE, content);
              TalentManager.loadTalents(context);
              break;
            case CareerManager.CAREERS_FILE:
              writeStringToFile(context, CareerManager.CAREERS_FILE, content);
              CareerManager.loadCareers(context);
              break;
            case ForcePowerManager.FORCE_POWERS_FILE:
              writeStringToFile(context, ForcePowerManager.FORCE_POWERS_FILE, content);
              ForcePowerManager.loadForcePowers(context);
              break;
            }
          }
        });
      }
    }

    writeVersionInfo(context);
  }

  private static void writeVersionInfo(@NotNull Context context) {
    try {
      JSONArray versionInfo = new JSONArray();
      for (Map.Entry<String, Integer> entry : fileVersions.entrySet()) {
        JSONObject version = new JSONObject();
        version.put(NAME_KEY, entry.getKey());
        version.put(VERSION_KEY, entry.getValue());
        versionInfo.put(version);
      }

      writeStringToFile(context, VERSIONS_FILE, versionInfo.toString(2));
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error writing version info to file.", e);
    }
  }

  private static void parseVersionInfo(@NotNull String content, Map<String, Integer> versionMap) {
    try {
      JSONArray versions = new JSONArray(content);
      for (int i = 0; i < versions.length(); i++) {
        JSONObject version = versions.getJSONObject(i);
        versionMap.put(version.getString(NAME_KEY), version.getInt(VERSION_KEY));
      }
    }
    catch (JSONException e) {
      Log.e(LOG_TAG, "Error parsing Version content.", e);
    }
  }
}
