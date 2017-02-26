package org.raincitygamers.holocron.rules.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

abstract class ManagerBase {
  private static final String BASE_URL = "https://raw.githubusercontent.com/j8s0n/Holocron/master/DataFiles/";
  private static final String LOG_TAG = ManagerBase.class.getSimpleName();

  static void getFileContent(@NotNull final Context context, @NotNull final String fileName, boolean checkInternet,
                             @NotNull final ContentParser parser) {
    if (!Arrays.asList(context.fileList()).contains(fileName) && checkInternet) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          parser.parse(downloadLatestFile(context, fileName));
        }
      });
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
    try {
      InputStream fis = new URL(url).openStream();
      String content = readInputStream(fis);
      fis.close();

      FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
      fos.write(content.getBytes());
      fos.close();

      return content;
    }
    catch (MalformedURLException e) {
      Log.e(LOG_TAG, "Malformed URL: " + url, e);
    }
    catch (IOException e) {
      Log.e(LOG_TAG, "Stream error.", e);
    }

    return "";
  }

  interface ContentParser {
    void parse(@NotNull String content);
  }
}
