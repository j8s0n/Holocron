package com.moosecoders.holocron.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class ActivityBase extends AppCompatActivity {
  public static final String EDIT_ACTIVE_CHARACTER = "edit_active_character";
  public static final String CURRENT_OPEN_PAGE = "current_open_page";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String title = getTitleString();
    if (title != null) {
      setTitle(title);
    }
  }

  protected void displayPermissionAlert(String type) {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("No " + type + " Access");
    alertDialog.setMessage("Please add Permission for this app to access " + type + " under Settings.");
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take Me There",
                          new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              displayAppSettings();
                            }
                          });

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No Thanks",
                          new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                            }
                          });

    alertDialog.show();
  }

  private void displayAppSettings() {
    Intent i = new Intent();
    i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
    i.addCategory(Intent.CATEGORY_DEFAULT);
    i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(i);
  }

  protected boolean permissionGranted(String perm) {
    return PackageManager.PERMISSION_GRANTED ==
           checkPermission(perm, android.os.Process.myPid(), android.os.Process.myUid());

  }

  protected abstract String getTitleString();

  protected interface CommandAction {
    void act();
  }

  @RequiredArgsConstructor(suppressConstructorProperties = true)
  protected static class DrawerCommand {
    @Getter private final String label;
    @Getter private final CommandAction action;
  }
}
