package org.raincitygamers.holocron.ui.pages.gear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.InventoryItem;

import java.util.List;

public class GearArrayAdapter extends ArrayAdapter<InventoryItem> {
  public GearArrayAdapter(Context context, List<InventoryItem> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    InventoryItem item = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.gear_list_item, parent, false);
      viewHolder.name = (TextView) convertView.findViewById(R.id.item_name);
      viewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
      viewHolder.location = (TextView) convertView.findViewById(R.id.location);
      viewHolder.description = (TextView) convertView.findViewById(R.id.description);
      viewHolder.encumbrance = (TextView) convertView.findViewById(R.id.encumbrance);
      viewHolder.countEncumbrance = (CheckBox) convertView.findViewById(R.id.count_encumbrance);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.name.setText(item.getName());
    if (item.getQuantity() > 1) {
      viewHolder.quantity.setText(String.format("(%d)", item.getQuantity()));
    }
    else {
      viewHolder.quantity.setText("");
    }

    viewHolder.location.setText(item.getLocation());
    viewHolder.description.setText(item.getDescription());
    viewHolder.encumbrance.setText(String.format("%d", item.getEncumbrance()));
    viewHolder.countEncumbrance.setChecked(item.isCountEncumbrance());
    if (viewHolder.countEncumbrance.isChecked()) {
      viewHolder.encumbrance.setAlpha(1.0f);
    }
    else {
      viewHolder.encumbrance.setAlpha(0.5f);
    }

    return convertView;
  }

  private static class ViewHolder {
    TextView name;
    TextView quantity;
    TextView location;
    TextView description;
    TextView encumbrance;
    CheckBox countEncumbrance;
  }
}
