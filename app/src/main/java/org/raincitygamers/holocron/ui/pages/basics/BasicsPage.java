package org.raincitygamers.holocron.ui.pages.basics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.ui.display.DisplayPage;
import org.raincitygamers.holocron.ui.pages.rowdata.RowData;

import java.util.ArrayList;
import java.util.List;

public class BasicsPage extends DisplayPage {
  private List<RowData> rowData = new ArrayList<>();
  private BasicsArrayAdapter arrayAdapter;

  public BasicsPage() {
  }

  @Override
  public String getTitle() {
    return "Basics";
  }

  @Override
  public void onResume() {
    super.onResume();
    displayBasics();
  }

  private void displayBasics() {
    rowData.clear();
    rowData.addAll(CharacterManager.getActiveCharacter().getBasics());
    arrayAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_basics_page, container, false);

    ListView basicsListView = (ListView) result.findViewById(R.id.basics_group_list);
    arrayAdapter = new BasicsArrayAdapter(getActivity(), rowData);
    basicsListView.setAdapter(arrayAdapter);

    return result;
  }
}
