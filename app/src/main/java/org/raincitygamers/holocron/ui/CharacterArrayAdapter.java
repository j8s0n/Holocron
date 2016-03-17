package org.raincitygamers.holocron.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;

import java.util.List;

/**
 * Created by jason on 2016-03-13.
 */
public class CharacterArrayAdapter extends ArrayAdapter<Character> {
  public CharacterArrayAdapter(Context context, List<Character> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Character character = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.selector_list_item, parent, false);
      viewHolder.characterName = (TextView) convertView.findViewById(R.id.character_selection_name);
      viewHolder.characterCareer = (TextView) convertView.findViewById(R.id.character_selection_career);
      viewHolder.lastAccessDate = (TextView) convertView.findViewById(R.id.character_selection_date);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.characterName.setText(character.getName());
    viewHolder.characterCareer.setText(character.getCareer().getName());
    viewHolder.lastAccessDate.setText(character.getTimestampString());
    return  convertView;
  }

  private static class ViewHolder {
    TextView characterName;
    TextView characterCareer;
    TextView lastAccessDate;
  }
}
