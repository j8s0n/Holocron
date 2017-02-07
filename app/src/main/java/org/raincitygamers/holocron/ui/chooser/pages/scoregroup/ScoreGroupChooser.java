package org.raincitygamers.holocron.ui.chooser.pages.scoregroup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.ui.chooser.ChooserBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ScoreGroupChooser extends ChooserBase {
  private static final String LOG_TAG = ScoreGroupChooser.class.getSimpleName();
  private List<ScoreRating> scoreRatings = new ArrayList<>();
  private ScoreGroupArrayAdapter scoreArrayAdapter;

  protected abstract Collection<ScoreRating> getScoreRatings();

  public ScoreGroupChooser() {
    Log.i(LOG_TAG, "ScoreGroupChooser()");
  }

  @Override
  public void onResume() {
    super.onResume();
    displayScores();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.choose_score_group, container, false);

    ListView scoreListView = (ListView) result.findViewById(R.id.score_group_list);
    scoreArrayAdapter = new ScoreGroupArrayAdapter(getActivity(), scoreRatings);
    scoreListView.setAdapter(scoreArrayAdapter);

    return result;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  public Map<String, Integer> getScores() {
    return scoreArrayAdapter.getScores();
  }

  private void displayScores() {
    scoreRatings.clear();
    scoreRatings.addAll(getScoreRatings());
    scoreArrayAdapter.notifyDataSetChanged();
  }
}
