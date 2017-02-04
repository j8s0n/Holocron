package org.raincitygamers.holocron.ui.pages.abilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.abilities.Ability;

import java.util.List;

public class AbilityArrayAdapter extends ArrayAdapter<Ability> {
  public AbilityArrayAdapter(Context context, List<Ability> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Ability ability = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.ability_list_item, parent, false);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.source = (TextView) convertView.findViewById(R.id.source);
      viewHolder.description = (TextView) convertView.findViewById(R.id.description);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.name.setText(ability.getName());
    viewHolder.description.setText(ability.getDescription());
    viewHolder.source.setText(String.format("%s %d", ability.getSource(), ability.getTier()));

    return convertView;
  }

  private static class ViewHolder {
    TextView name;
    TextView source;
    TextView description;
  }
}
