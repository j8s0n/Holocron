package org.raincitygamers.holocron.ui.display.pages.gear;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.CreditEditorActivity;
import org.raincitygamers.holocron.ui.InventoryEditorActivity;
import org.raincitygamers.holocron.ui.display.pages.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.KeyValueRowData.KvPair;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;

import java.util.List;
import java.util.Locale;

class GearArrayAdapter extends ArrayAdapter<RowData> {
  private static final float IGNORED_ENCUMBRANCE = 0.2f;
  private TextView encumbrance;

  GearArrayAdapter(Context context, List<RowData> objects) {
    super(context, -1, objects);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    // Handles Key-Value and Inventory Row Data
    RowData rowData = getItem(position);
    if (rowData != null) {
      switch (rowData.getType()) {
      case KEY_VALUE:
        return displayKeyValuePair(convertView, parent, ((KeyValueRowData) rowData).getPair());
      case INVENTORY:
        return displayInventory(convertView, parent, ((InventoryItemRowData) rowData).getItem());
      case BUTTON:
        return displayButton(convertView, parent, ((ButtonRowData) rowData).getButtonText());
      default:
        throw new IllegalStateException(String.format(Locale.US, "Row type %s is not valid here.", rowData.getType()));
      }
    }

    throw new IllegalStateException(String.format(Locale.US, "No item at position %d.", position));
  }

  @NotNull
  private View displayInventory(View convertView, final ViewGroup parent, final InventoryItem item) {
    final ViewHolder viewHolder;

    if (convertView == null || !convertView.getTag().equals(RowData.Type.INVENTORY)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_inventory, parent, false);
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

    String name = item.getName();
    if (name.isEmpty()) {
      name = "<Unnamed Item>";
    }

    name = String.format(Locale.US, "%-8s", name);

    viewHolder.name.setText(name);
    if (item.getQuantity() > 1) {
      viewHolder.quantity.setText(String.format(Locale.US, "(%d)", item.getQuantity()));
    }
    else {
      viewHolder.quantity.setText("   ");
    }

    viewHolder.location.setText(item.getLocation());
    viewHolder.description.setText(item.getDescription());
    viewHolder.encumbrance.setText(String.format(Locale.US, "%d", item.getEncumbrance()));
    viewHolder.countEncumbrance.setChecked(item.isCountEncumbrance());
    if (viewHolder.countEncumbrance.isChecked()) {
      viewHolder.encumbrance.setAlpha(1.0f);
    }
    else {
      viewHolder.encumbrance.setAlpha(IGNORED_ENCUMBRANCE);
    }

    setClickHandlers(viewHolder, item);
    return convertView;
  }

  private void setClickHandlers(@NotNull final ViewHolder viewHolder, @NotNull final InventoryItem item) {

    viewHolder.countEncumbrance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        item.setCountEncumbrance(isChecked);
        viewHolder.encumbrance.setAlpha(isChecked ? 1.0f : IGNORED_ENCUMBRANCE);
        updateEncumbrance();
      }
    });

    viewHolder.quantity.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        item.setQuantity(Math.max(0, item.getQuantity() - 1));
        viewHolder.quantity.setText(String.format(Locale.US, "(%d)", item.getQuantity()));
        updateEncumbrance();
        return true;
      }
    });

    viewHolder.quantity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        item.setQuantity(item.getQuantity() + 1);
        viewHolder.quantity.setText(String.format(Locale.US, "(%d)", item.getQuantity()));
        updateEncumbrance();
      }
    });

    viewHolder.name.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Intent intent = new Intent(getContext(), InventoryEditorActivity.class);
        int index = CharacterManager.getActiveCharacter().getInventory().indexOf(item);
        intent.putExtra(InventoryEditorActivity.INVENTORY_ITEM_TO_EDIT, index);
        getContext().startActivity(intent);

        return true;
      }
    });
  }

  private void updateEncumbrance() {
    if (encumbrance != null) {
      Character pc = CharacterManager.getActiveCharacter();
      encumbrance.setText(String.format(Locale.US, "%d / %d", pc.getEncumbrance(), pc.getEncumbranceThreshold()));
    }
  }

  @NotNull
  private View displayKeyValuePair(View convertView, ViewGroup parent, KvPair pair) {
    ViewHolder viewHolder;
    if (convertView == null || !convertView.getTag().equals(RowData.Type.KEY_VALUE)) {
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

    if (pair.getKey().equals("Credits")) {
      convertView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          Intent intent = new Intent(getContext(), CreditEditorActivity.class);
          getContext().startActivity(intent);
          return true;
        }
      });
    }
    else if (pair.getKey().equals("Encumbrance")) {
      encumbrance = viewHolder.value;
    }

    return convertView;
  }

  private View displayButton(View convertView, ViewGroup parent, String buttonText) {
    ViewHolder viewHolder;
    if (convertView == null || !convertView.getTag().equals(RowData.Type.BUTTON)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_button, parent, false);
      viewHolder.button = (Button) convertView.findViewById(R.id.button);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.button.setText(buttonText);
    viewHolder.button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), InventoryEditorActivity.class);
        intent.putExtra(InventoryEditorActivity.INVENTORY_ITEM_TO_EDIT, -1);
        getContext().startActivity(intent);
      }
    });
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

    Button button;
  }
}
