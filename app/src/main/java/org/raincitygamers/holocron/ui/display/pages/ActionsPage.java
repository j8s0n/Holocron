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
import org.raincitygamers.holocron.ui.display.SkillActionEditorActivity;
import org.raincitygamers.holocron.ui.display.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;

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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result = inflater.inflate(R.layout.display_actions, container, false);

    ListView actionsListView = (ListView) result.findViewById(R.id.actions_group_list);
    arrayAdapter = new DisplayArrayAdapter(getActivity(), rowData, this);
    actionsListView.setAdapter(arrayAdapter);

    return result;
  }

  private void displayActions() {
    rowData.clear();
    rowData.addAll(CharacterManager.getActiveCharacter().getActions());
    rowData.add(ButtonRowData.of("Add Skill", new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SkillActionEditorActivity.class);
        getActivity().startActivity(intent);
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
  public void invalidate() {
    displayActions();
  }
}

