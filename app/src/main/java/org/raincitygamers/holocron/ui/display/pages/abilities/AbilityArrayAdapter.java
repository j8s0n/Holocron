package org.raincitygamers.holocron.ui.display.pages.abilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.traits.Ability;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData.KvPair;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SectionRowData;

import java.util.List;

public class AbilityArrayAdapter extends ArrayAdapter<RowData> {
  public AbilityArrayAdapter(Context context, List<RowData> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    RowData rowData = getItem(position);

    switch (rowData.getType()) {
    case ABILITY:
      return displayAbility(convertView, parent, ((AbilityRowData)rowData).getAbility());
    case KEY_VALUE:
      return displayKeyValuePair(convertView, parent, ((KeyValueRowData)rowData).getPair());
    case SECTION_ID:
      return displaySection(convertView, parent, ((SectionRowData)rowData).getSectionId());
    default:
      // TODO
      return null;
    }
  }

  @NotNull
  private View displayAbility(View convertView, ViewGroup parent, Ability ability) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_ability, parent, false);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.tier = (TextView) convertView.findViewById(R.id.tier);
      viewHolder.description = (TextView) convertView.findViewById(R.id.description);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.name.setText(ability.getName());
    viewHolder.description.setText(ability.getDescription());
    viewHolder.tier.setText(String.format("%d", ability.getTier()));
    return convertView;
  }

  @NotNull
  private View displaySection(View convertView, ViewGroup parent, String sectionLabel) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_section, parent, false);
      viewHolder.sectionLabel = (TextView) convertView.findViewById(R.id.section_label);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.sectionLabel.setText(sectionLabel);
    return convertView;
  }

  @NotNull
  private View displayKeyValuePair(View convertView, ViewGroup parent, KvPair pair) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_key_value, parent, false);
      viewHolder.key = (TextView) convertView.findViewById(R.id.key);
      viewHolder.value = (TextView) convertView.findViewById(R.id.value);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.key.setText(pair.getKey());
    viewHolder.value.setText(pair.getValue());
    return convertView;
  }

  private static class ViewHolder {
    TextView name;
    TextView tier;
    TextView description;

    TextView sectionLabel;

    TextView key;
    TextView value;
  }
}
