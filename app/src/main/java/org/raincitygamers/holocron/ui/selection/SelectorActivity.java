package org.raincitygamers.holocron.ui.selection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.Character.Summary;
import org.raincitygamers.holocron.rules.managers.CareerManager;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.managers.ForcePowerManager;
import org.raincitygamers.holocron.rules.managers.SkillManager;
import org.raincitygamers.holocron.rules.managers.TalentManager;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.chooser.ChooserActivity;
import org.raincitygamers.holocron.ui.display.DisplayActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SelectorActivity extends ActivityBase {
  private List<Summary> characters = new ArrayList<>();
  private CharacterArrayAdapter characterArrayAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selector);

    checkPermissions();
    CareerManager.loadCareers(this);
    SkillManager.loadSkills(this);
    TalentManager.loadTalents(this);
    ForcePowerManager.loadForcePowers(this);
    CharacterManager.loadCharacters(this);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    assert fab != null;
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(SelectorActivity.this, ChooserActivity.class);
        startActivity(intent);
      }
    });


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

    characterListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SelectorActivity.this, DisplayActivity.class);
        Summary summary = characters.get(position);

        CharacterManager.loadCharacterToActiveState(SelectorActivity.this, summary.getCharacterId());
        startActivity(intent);
      }
    });
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
    // TODO????
    // Create an async and wait for the files to all load, before displaying characters.
  }

  private void displayCharacters() {
    characters.clear();
    for (UUID characterId : CharacterManager.getCharacterIds()) {
      characters.add(CharacterManager.getCharacterSummary(characterId));
    }

    Collections.sort(characters, Character.getMostRecentAccessComparator());
    characterArrayAdapter.notifyDataSetChanged();
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
