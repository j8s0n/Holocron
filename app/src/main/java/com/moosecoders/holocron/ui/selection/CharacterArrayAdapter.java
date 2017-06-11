package com.moosecoders.holocron.ui.selection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moosecoders.holocron.R;
import com.moosecoders.holocron.rules.character.Character.Summary;

import java.util.List;

class CharacterArrayAdapter extends ArrayAdapter<Summary> {
  CharacterArrayAdapter(Context context, List<Summary> objects) {
    super(context, -1, objects);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    Summary character = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_selector, parent, false);
      viewHolder.characterName = (TextView) convertView.findViewById(R.id.character_selection_name);
      viewHolder.characterCareer = (TextView) convertView.findViewById(R.id.character_selection_career);
      viewHolder.lastAccessDate = (TextView) convertView.findViewById(R.id.character_selection_date);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    if (character != null) {
      viewHolder.characterName.setText(character.getName());
      viewHolder.characterCareer.setText(character.getBlurb());
      viewHolder.lastAccessDate.setText(character.getTimestampString());

    }
    return  convertView;
  }

  private static class ViewHolder {
    TextView characterName;
    TextView characterCareer;
    TextView lastAccessDate;
  }
}
