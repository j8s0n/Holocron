package com.moosecoders.holocron.ui.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.ActivityBase;
import com.moosecoders.holocron.ui.FragmentInvalidator;
import com.moosecoders.holocron.ui.display.rowdata.ButtonRowData;
import com.moosecoders.holocron.ui.display.rowdata.KeyValueRowData;
import com.moosecoders.holocron.ui.display.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WealthTrackerActivity extends ActivityBase implements FragmentInvalidator {
  private DisplayArrayAdapter arrayAdapter;
  private List<RowData> rowData = new ArrayList<>();

  private final Character pc = CharacterManager.getActiveCharacter();
  private List<Pair<String, Integer>> wealth = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.wealth_tracker);

    for (Map.Entry<String, Integer> entry : pc.getWealth().entrySet()) {
      wealth.add(Pair.create(entry.getKey(), entry.getValue()));
    }

    arrayAdapter = new DisplayArrayAdapter(this, rowData, this);
    ListView conditions = (ListView) findViewById(R.id.wealth_entries);
    conditions.setAdapter(arrayAdapter);
    addListItems();
  }

  private void addListItems() {
    rowData.clear();
    for (int i = 0; i < wealth.size(); i++) {
      final Pair<String, Integer> entry = wealth.get(i);
      final int finalI = i;
      rowData.add(KeyValueRowData.of(entry.first, String.valueOf(entry.second), R.drawable.ic_credit, new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          Intent intent = new Intent(WealthTrackerActivity.this, WealthEditorActivity.class);
          intent.putExtra(WealthEditorActivity.ID, finalI);
          intent.putExtra(WealthEditorActivity.LOCATION, entry.first);
          intent.putExtra(WealthEditorActivity.AMOUNT, entry.second);
          startActivityForResult(intent, WealthEditorActivity.EDIT_WEALTH_ENTRY);
          return true;
        }
      }));
    }

    rowData.add(ButtonRowData.of("Add", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(WealthTrackerActivity.this, WealthEditorActivity.class);
        intent.putExtra(WealthEditorActivity.ID, -1);
        intent.putExtra(WealthEditorActivity.LOCATION, "");
        intent.putExtra(WealthEditorActivity.AMOUNT, 0);
        startActivityForResult(intent, WealthEditorActivity.EDIT_WEALTH_ENTRY);
      }
    }));

    rowData.add(ButtonRowData.of("Done", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pc.getWealth().clear();
        for (Pair<String, Integer> entry : wealth) {
          if (!entry.first.isEmpty()) {
            pc.getWealth().put(entry.first, entry.second);
          }
        }

        finish();
      }
    }));
    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
    case WealthEditorActivity.EDIT_WEALTH_ENTRY:
      if (resultCode == Activity.RESULT_OK) {
        int id = data.getIntExtra(WealthEditorActivity.ID, -1);
        String location = data.getStringExtra(WealthEditorActivity.LOCATION);
        int amount = data.getIntExtra(WealthEditorActivity.AMOUNT, 0);
        if (location == null || location.isEmpty()) {
          if (id >= 0 && id < wealth.size()) {
            wealth.remove(id);
            invalidate();
          }
        }
        else if (id == -1) {
          wealth.add(Pair.create(location, amount));
          invalidate();
        }
        else if (id >= 0 && id < wealth.size()) {
          wealth.set(id, Pair.create(location, amount));
          invalidate();
        }
      }

      break;
    }
  }

  @NotNull
  @Override
  protected String getTitleString() {
    return CharacterManager.getActiveCharacter().getName() + " - Wealth Tracker";
  }

  @Override
  public void invalidate() {
    addListItems();
  }
}
