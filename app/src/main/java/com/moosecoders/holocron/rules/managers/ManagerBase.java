package com.moosecoders.holocron.rules.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

abstract class ManagerBase {
  private static final String BASE_URL = "https://raw.githubusercontent.com/j8s0n/Holocron/master/DataFiles/";
  private static final String LOG_TAG = ManagerBase.class.getSimpleName();

  static void getFileContent(@NotNull final Context context, @NotNull final String fileName, FileSource source,
                             @NotNull final ContentParser parser) {
    boolean localCopyExists = Arrays.asList(context.fileList()).contains(fileName);
    if ((!localCopyExists && source.equals(FileSource.LOCAL_FIRST)) ||
        source.equals(FileSource.DOWNLOAD_ONLY)) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          parser.parse(downloadLatestFile(context, fileName));
        }
      });
    }
    else if (!localCopyExists && source.equals(FileSource.LOCAL_ONLY)) {
      parser.parse("");
    }
    else {
      try {
        FileInputStream fis = context.openFileInput(fileName);
        parser.parse(readInputStream(fis));
        fis.close();
      }
      catch (FileNotFoundException e) {
        Log.e(LOG_TAG, "Unable to find file: " + fileName, e);
      }
      catch (IOException e) {
        Log.e(LOG_TAG, "Unable to read or close file: " + fileName, e);
      }
    }
  }

  @NotNull
  static String readInputStream(@NotNull InputStream is) throws IOException {
    byte[] buffer = new byte[65536];
    StringBuilder sb = new StringBuilder();
    int count;
    while ((count = is.read(buffer)) >= 0) {
      sb.append(new String(buffer, 0, count));
    }

    is.close();
    return sb.toString();
  }

  @NotNull
  private static String downloadLatestFile(@NotNull Context context, @NotNull String fileName) {
    String url = BASE_URL + fileName;
    String content = readUrlToString(url);
    writeStringToFile(context, fileName, content);
    return content;
  }

  static void writeStringToFile(@NotNull Context context, @NotNull String fileName, @NotNull String content) {
    try {
      FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
      fos.write(content.getBytes());
      fos.close();
    }
    catch (IOException e) {
      Log.e(LOG_TAG, "Error writing to file: " + fileName, e);
    }
  }

  @NotNull
  private static String readUrlToString(@NotNull String url) {
    try {
      InputStream fis = new URL(url).openStream();
      String content = readInputStream(fis);
      fis.close();
      return content;
    }
    catch (IOException e) {
      Log.e(LOG_TAG, "Error reading URL: " + url, e);
      return "";
    }
  }

  interface ContentParser {
    void parse(@NotNull String content);
  }

  enum FileSource {
    LOCAL_FIRST,
    LOCAL_ONLY,
    DOWNLOAD_ONLY
  }
}
