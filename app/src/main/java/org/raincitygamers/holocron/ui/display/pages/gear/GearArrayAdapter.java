package org.raincitygamers.holocron.ui.display.pages.gear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.CharacterManager;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData.KvPair;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.List;

public class GearArrayAdapter extends ArrayAdapter<RowData> {
  private static final float IGNORED_ENCUMBRANCE = 0.2f;
  private TextView encumbrance;
  public GearArrayAdapter(Context context, List<RowData> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // Handles Key-Value and Inventory Row Data
    RowData rowData = getItem(position);
    switch (rowData.getType()) {
    case KEY_VALUE:
      return displayKeyValuePair(convertView, parent, ((KeyValueRowData) rowData).getPair());
    case INVENTORY:
      return displayInventory(convertView, parent, ((InventoryItemRowData) rowData).getItem());
    default:
      return null;
    }
  }

  @NotNull
  private View displayInventory(View convertView, final ViewGroup parent, final InventoryItem item) {
    final ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.gear_list_item, parent, false);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
      viewHolder.location = (TextView) convertView.findViewById(R.id.tier);
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
      viewHolder.encumbrance.setAlpha(IGNORED_ENCUMBRANCE);
    }

    viewHolder.countEncumbrance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        item.setCountEncumbrance(isChecked);
        viewHolder.encumbrance.setAlpha(isChecked ? 1.0f : IGNORED_ENCUMBRANCE);
        if (encumbrance != null) {
          Character pc = CharacterManager.getActiveCharacter();
          encumbrance.setText(String.format("%d / %d", pc.getEncumbrance(), pc.getEncumbranceThreshold()));
        }
      }
    });

    return convertView;
  }

  @NotNull
  private View displayKeyValuePair(View convertView, ViewGroup parent, KvPair pair) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.key_value_list_item, parent, false);
      viewHolder.key = (TextView) convertView.findViewById(R.id.key);
      viewHolder.value = (TextView) convertView.findViewById(R.id.value);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.key.setText(pair.getKey());
    viewHolder.value.setText(pair.getValue());
    encumbrance = viewHolder.value;
    return convertView;
  }

  private static class ViewHolder {
    TextView name;
    TextView quantity;
    TextView location;
    TextView description;
    TextView encumbrance;
    CheckBox countEncumbrance;

    TextView key;
    TextView value;
  }
}
