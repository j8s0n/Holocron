package org.raincitygamers.holocron.ui.display.pages.actions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.ContentPage;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public class ActionsPage extends ContentPage {
  private List<RowData> rowData = new ArrayList<>();
  private ActionsArrayAdapter arrayAdapter;

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
    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.display_actions, container, false);

    ListView actionsListView = (ListView) result.findViewById(R.id.actions_group_list);
    arrayAdapter = new ActionsArrayAdapter(getActivity(), rowData);
    actionsListView.setAdapter(arrayAdapter);

    return result;
  }
}

