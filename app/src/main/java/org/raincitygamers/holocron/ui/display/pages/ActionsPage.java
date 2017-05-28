package org.raincitygamers.holocron.ui.display.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.ConditionEditorActivity;
import org.raincitygamers.holocron.ui.display.DisplayArrayAdapter;
import org.raincitygamers.holocron.ui.display.pages.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public class ActionsPage extends ContentPage implements FragmentInvalidator {
  private List<RowData> rowData = new ArrayList<>();
  private DisplayArrayAdapter arrayAdapter;

  public ActionsPage() {
  }

  @Override
  public String getTitle() {
    return "Actions";
  }

  @Override
  public void onResume() {
    super.onResume();
    displayActions();
  }

  private void displayActions() {
    rowData.clear();
    rowData.addAll(CharacterManager.getActiveCharacter().getActions());
    rowData.add(ButtonRowData.of("Add Skill", new OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO: No intermediary.
        // Have an add button at the bottom.
        // Bring up the editor.
        // Have a remove button on the editor.
        // Long press a skill action to open it in the editor.
        // In the editor, if there are no conditions on the PC, add a note to add conditions.

        // Intent intent = new Intent(getActivity(), SkillActionEditorActivity.class);
        // getActivity().startActivity(intent);
        // BETTER IDEA!!!!!!!!!!!!!! ????

        // FUCK NO!!!!
        // FUCK NO!!!!
        // FUCK NO!!!!
        // FUCK NO!!!!
        // Just use the drawer activity.

        //  Create the list of current skill actions, with the edit, remove, and add buttons as above.
        //  For the editor, use a static-ish activity. Show the name, skill spinner, char spinner,
        //  and either:
        //    a list of cells for conditions
        //    - each cell is a condition spinner and grid of dice icons, counts, and +/- buttons.
        //    same cells but programatically added to the activity
        /*
        Intent intent = new Intent(getActivity(), SkillActionEditorActivity.class);
        getActivity().startActivity(intent);
        */
      }
    }));

    rowData.add(ButtonRowData.of("Edit Conditions", new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ConditionEditorActivity.class);
        getActivity().startActivity(intent);
      }
    }));

    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_actions, container, false);

    ListView actionsListView = (ListView) result.findViewById(R.id.actions_group_list);
    arrayAdapter = new DisplayArrayAdapter(getActivity(), rowData, this);
    actionsListView.setAdapter(arrayAdapter);

    return result;
  }

  @Override
  public void invalidate() {
    displayActions();
  }
}

