package org.raincitygamers.holocron.ui.selection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.common.collect.EvictingQueue;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Character.Summary;
import org.raincitygamers.holocron.rules.managers.CareerManager;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.DownloadManager;
import org.raincitygamers.holocron.rules.managers.ForcePowerManager;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.managers.TalentManager;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;
import org.raincitygamers.holocron.ui.display.DisplayActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class SelectorActivity extends ActivityBase implements ConnectionCallbacks, OnConnectionFailedListener {
  private static final int REQ_OPEN = 0;
  private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
  private static final String LOG_TAG = SelectorActivity.class.getSimpleName();
  private static final int LOGIN_QUEUE_SIZE = 4;

  private List<Summary> characters = new ArrayList<>();
  private CharacterArrayAdapter characterArrayAdapter;
  private GoogleApiClient googleApiClient;
  private Queue<Long> loginAttempts = EvictingQueue.create(LOGIN_QUEUE_SIZE);
  private Menu optionsMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.selector);

    checkPermissions();
    DownloadManager.checkForUpdates(this);
    CareerManager.loadCareers(this);
    SkillManager.loadSkills(this);
    TalentManager.loadTalents(this);
    TalentManager.loadTalentDescriptions(this);
    ForcePowerManager.loadForcePowers(this);
    CharacterManager.loadCharacters(this);

    FloatingActionButton newCharacterButton = (FloatingActionButton) findViewById(R.id.new_character_button);
    assert newCharacterButton != null;
    newCharacterButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(SelectorActivity.this, ChooserActivity.class);
        startActivity(intent);
      }
    });

    FloatingActionButton openDriveButton = (FloatingActionButton) findViewById(R.id.open_drive_button);
    assert openDriveButton != null;
    openDriveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectFileOnDrive();
      }
    });

    showConnectToDriveButton(usingGoogleDrive());

    ListView characterListView = (ListView) findViewById(R.id.character_selection_list);
    characterArrayAdapter = new CharacterArrayAdapter(this, characters);
    final SwipeActionAdapter swipeActionAdapter = new SwipeActionAdapter(characterArrayAdapter);
    swipeActionAdapter.setListView(characterListView);
    characterListView.setAdapter(swipeActionAdapter);
    swipeActionAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.row_bg_left)
        .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
        .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right)
        .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);

    swipeActionAdapter.setSwipeActionListener(new SwipeActionHandler(swipeActionAdapter, characters, this));
    swipeActionAdapter.setFixedBackgrounds(false);
    swipeActionAdapter.setFadeOut(false);
    swipeActionAdapter.setFarSwipeFraction(0.7f);

    characterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SelectorActivity.this, DisplayActivity.class);
        Summary summary = characters.get(position);
        CharacterManager.loadCharacterToActiveState(SelectorActivity.this, summary.getCharacterId());
        startActivity(intent);
      }
    });
  }

  @Override
  protected String getTitleString() {
    return null;
  }

  private void checkPermissions() {
    if (!permissionGranted(Manifest.permission.ACCESS_NETWORK_STATE) ||
        !permissionGranted(Manifest.permission.INTERNET)) {
      displayPermissionAlert("Network");
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    displayCharacters();
    if (getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.use_google_drive_label), false)) {
      connectToDrive();
    }
  }

  @Override
  protected void onPause() {
    if (googleApiClient != null) {
      googleApiClient.disconnect();
    }

    super.onPause();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.use_google_drive_switch:
      boolean useDrive = !item.isChecked();
      item.setChecked(useDrive);
      if (useDrive) {
        connectToDrive();
      }

      savePreferenceBoolean(R.string.use_google_drive_label, useDrive);
      showConnectToDriveButton(useDrive);
      return true;

    default:
      return super.onOptionsItemSelected(item);
    }
  }

  private void savePreferenceBoolean(int stringId, boolean value) {
    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
    editor.putBoolean(getString(stringId), value);
    editor.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    optionsMenu = menu;
    getMenuInflater().inflate(R.menu.menu_main, menu);
    MenuItem useGoogleDriveSwitch = menu.findItem(R.id.use_google_drive_switch);
    useGoogleDriveSwitch.setChecked(usingGoogleDrive());
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case REQ_OPEN:
      if (resultCode == RESULT_OK) {
        googleApiClient.connect();
        final DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        if (driveId.getResourceType() != DriveId.RESOURCE_TYPE_FILE) {
          Toast.makeText(this, "Invalid file.", Toast.LENGTH_LONG).show();
          return;
        }

        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            Intent intent = new Intent(SelectorActivity.this, DisplayActivity.class);
            intent.putExtra(DisplayActivity.CHARACTER_CONTENT, getContentsOfDriveFile(driveId));
            startActivity(intent);
          }
        });
      }
      else if (resultCode == RESULT_CANCELED) {
        return;
      }
      else {
        Toast.makeText(this, "Unable to connect to Google Drive. Cannot open characters.", Toast.LENGTH_LONG).show();
        savePreferenceBoolean(R.string.use_google_drive_label, false);
      }
    case RESOLVE_CONNECTION_REQUEST_CODE:
      if (resultCode == RESULT_OK) {
        googleApiClient.connect();
      }
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      try {
        connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
      }
      catch (IntentSender.SendIntentException e) {
        Toast.makeText(this, "Unable to connect to Google Drive.", Toast.LENGTH_LONG).show();
        savePreferenceBoolean(R.string.use_google_drive_label, false);
      }
    }
    else {
      GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
      savePreferenceBoolean(R.string.use_google_drive_label, false);
    }
  }

  private boolean usingGoogleDrive() {
    return getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.use_google_drive_label), false);
  }

  private void showConnectToDriveButton(boolean show) {
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.open_drive_button);
    if (show) {
      fab.setVisibility(View.VISIBLE);
    }
    else {
      fab.setVisibility(View.INVISIBLE);
    }
  }

  private void connectToDrive() {
    long now = System.currentTimeMillis();
    loginAttempts.add(now);
    if (loginAttempts.size() == LOGIN_QUEUE_SIZE) {
      long then = loginAttempts.peek();
      if (now - then < 60000) {
        savePreferenceBoolean(R.string.use_google_drive_label, false);
        showConnectToDriveButton(false);
        MenuItem useGoogleDriveSwitch = optionsMenu.findItem(R.id.use_google_drive_switch);
        useGoogleDriveSwitch.setChecked(usingGoogleDrive());
        return;
      }
    }
    if (googleApiClient == null) {
      googleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Drive.API)
                            .addScope(Drive.SCOPE_FILE)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
    }
    if (!googleApiClient.isConnected()) {
      googleApiClient.connect();
    }
  }

  private void displayCharacters() {
    characters.clear();
    for (UUID characterId : CharacterManager.getCharacterIds()) {
      characters.add(CharacterManager.getCharacterSummary(characterId));
    }

    Collections.sort(characters, Character.getMostRecentAccessComparator());
    characterArrayAdapter.notifyDataSetChanged();
  }

  private void selectFileOnDrive() {
    if (!googleApiClient.isConnected()) {
      Toast.makeText(this, "Unable to connect to Google Drive.", Toast.LENGTH_LONG).show();
      return;
    }

    IntentSender i = Drive.DriveApi.newOpenFileActivityBuilder().build(googleApiClient);
    try {
      startIntentSenderForResult(i, REQ_OPEN, null, 0, 0, 0);
    }
    catch (IntentSender.SendIntentException e) {
      e.printStackTrace();
      Toast.makeText(this, "Unable to connect to Google Drive.", Toast.LENGTH_LONG).show();
    }
  }

  @NotNull
  private String getContentsOfDriveFile(@NotNull DriveId driveId) {
    DriveFile driveFile = driveId.asDriveFile();
    DriveContents contents = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await().getDriveContents();
    try {
      return readInputStream(contents.getInputStream());
    }
    catch (IOException e) {
      Log.e(LOG_TAG, "Error reading from drive file.", e);
      return "";
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

  private static class SwipeActionHandler implements SwipeActionAdapter.SwipeActionListener {
    private final SwipeActionAdapter swipeActionAdapter;
    private final List<Summary> characters;
    private final Context context;

    private SwipeActionHandler(@NotNull SwipeActionAdapter swipeActionAdapter, @NotNull List<Summary> characters,
                               @NotNull Context context) {
      this.swipeActionAdapter = swipeActionAdapter;
      this.characters = characters;
      this.context = context;
    }

    @Override
    public boolean hasActions(int position, SwipeDirection direction) {
      return direction.isLeft() || direction.isRight();
    }

    @Override
    public boolean shouldDismiss(int position, SwipeDirection direction) {
      return direction == SwipeDirection.DIRECTION_FAR_RIGHT;
    }

    @Override
    public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
      for (int i = 0; i < positionList.length; i++) {
        SwipeDirection direction = directionList[i];

        switch (direction) {
        case DIRECTION_FAR_LEFT:
          break;
        case DIRECTION_NORMAL_LEFT:
          break;
        case DIRECTION_FAR_RIGHT:
          removeEntry(positionList[i], context);
          break;
        case DIRECTION_NORMAL_RIGHT:
          break;
        }

        swipeActionAdapter.notifyDataSetChanged();
      }
    }

    private void removeEntry(int position, @NotNull Context context) {
      CharacterManager.removeCharacter(characters.get(position), context);
      characters.remove(position);
    }
  }
}
