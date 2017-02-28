package org.raincitygamers.holocron.ui.display;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ActivityBase;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.pages.rowdata.AdderRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.ConditionEditorRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public class ConditionEditorActivity extends ActivityBase implements FragmentInvalidator {
  DisplayArrayAdapter arrayAdapter;
  List<RowData> rowData = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_conditions);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("Conditions Editor");
    }

    arrayAdapter = new DisplayArrayAdapter(this, rowData, this);
    ListView conditions = (ListView) findViewById(R.id.conditions_list);
    conditions.setAdapter(arrayAdapter);
    listConditions();
  }

  private void listConditions() {
    rowData.clear();
    final Character pc = CharacterManager.getActiveCharacter();
    for (final String condition : pc.getAllConditions()) {
      rowData.add(ConditionEditorRowData.of(condition, new OnClickListener() {
        @Override
        public void onClick(View v) {
          pc.removeActionCondition(condition);
          invalidate();
        }
      }));
    }

    rowData.add(AdderRowData.of(new AdderRowData.AddPerformer() {
      @Override
      public void performAdd(@NotNull String thingToAdd) {
        pc.setActionConditionState(thingToAdd, false);
        invalidate();
      }
    }));

    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  public void invalidate() {
    listConditions();
  }
}
