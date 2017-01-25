package org.raincitygamers.holocron.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class ActivityBase extends AppCompatActivity {
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
}
