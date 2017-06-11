package com.moosecoders.holocron.ui.display;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.managers.CharacterManager;
import com.moosecoders.holocron.ui.ActivityBase;
import com.moosecoders.holocron.ui.FragmentInvalidator;
import com.moosecoders.holocron.ui.display.rowdata.AdderRowData;
import com.moosecoders.holocron.ui.display.rowdata.ConditionEditorRowData;
import com.moosecoders.holocron.ui.display.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public class ConditionEditorActivity extends ActivityBase implements FragmentInvalidator {
  private DisplayArrayAdapter arrayAdapter;
  private List<RowData> rowData = new ArrayList<>();
  private final Character pc = CharacterManager.getActiveCharacter();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_conditions);

    arrayAdapter = new DisplayArrayAdapter(this, rowData, this);
    ListView conditions = (ListView) findViewById(R.id.conditions_list);
    conditions.setAdapter(arrayAdapter);
    listConditions();
  }

  private void listConditions() {
    rowData.clear();
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
        if (!thingToAdd.isEmpty()) {
          pc.setActionConditionState(thingToAdd, false);
          invalidate();
        }
        else {
          Toast.makeText(ConditionEditorActivity.this, "Please enter a name.", Toast.LENGTH_LONG).show();
        }
      }
    }));

    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  public void invalidate() {
    listConditions();
  }

  @NotNull
  @Override
  protected String getTitleString() {
    return pc.getName() + " - Conditions Editor";
  }
}
