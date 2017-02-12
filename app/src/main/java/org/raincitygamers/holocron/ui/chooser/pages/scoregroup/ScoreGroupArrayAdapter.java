package org.raincitygamers.holocron.ui.chooser.pages.scoregroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.raincitygamers.holocron.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class ScoreGroupArrayAdapter extends ArrayAdapter<ScoreRating> {
  @Getter private final Map<String, Integer> scores = new HashMap<>();

  public ScoreGroupArrayAdapter(Context context, List<ScoreRating> objects) {
    super(context, -1, objects);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    final ScoreRating scoreRating = getItem(position);
    final ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_choose_score, parent, false);
      viewHolder.scoreLabel = (TextView) convertView.findViewById(R.id.force_rating_label);
      viewHolder.downButton = (TextView) convertView.findViewById(R.id.force_rating_down_button);
      viewHolder.scoreEntry = (TextView) convertView.findViewById(R.id.force_rating_entry);
      viewHolder.upButton = (TextView) convertView.findViewById(R.id.force_rating_up_button);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.scoreLabel.setText(scoreRating.getScoreLabel());
    viewHolder.scoreEntry.setText(String.format("%d", scoreRating.getScoreValue()));
    viewHolder.downButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int value = Integer.parseInt(viewHolder.scoreEntry.getText().toString());
        value = Math.max(scoreRating.getMinimumValue(), value - 1);
        scores.put(scoreRating.getScoreLabel(), value);
        viewHolder.scoreEntry.setText(String.format("%d", value));
      }
    });

    viewHolder.upButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int value = Integer.parseInt(viewHolder.scoreEntry.getText().toString());
        value = Math.min(scoreRating.getMaximumValue(), value + 1);
        scores.put(scoreRating.getScoreLabel(), value);
        viewHolder.scoreEntry.setText(String.format("%d", value));
      }
    });

    return convertView;
  }

  private static class ViewHolder {
    TextView scoreLabel;
    TextView downButton;
    TextView scoreEntry;
    TextView upButton;
  }
}
