package org.raincitygamers.holocron.ui.display.pages.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SectionRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.ToggleRowData;

import java.util.List;
import java.util.Locale;

class ActionsArrayAdapter extends ArrayAdapter<RowData> {
  ActionsArrayAdapter(Context context, List<RowData> objects) {
    super(context, -1, objects);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    RowData rowData = getItem(position);
    if (rowData != null) {
      switch (rowData.getType()) {
      case SECTION_ID:
        return displaySection(convertView, parent, ((SectionRowData) rowData).getSectionId());
      case TOGGLE:
        return displayToggle(convertView, parent, ((ToggleRowData) rowData));
      default:
        throw new IllegalStateException(String.format(Locale.US, "Row type %s is not valid here.", rowData.getType()));
      }
    }

    throw new IllegalStateException(String.format(Locale.US, "No item at position %d.", position));
  }

  @NotNull
  private View displaySection(View convertView, ViewGroup parent, String sectionLabel) {
    ViewHolder viewHolder;
    if (convertView == null || !convertView.getTag().equals(RowData.Type.SECTION_ID)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_section, parent, false);
      viewHolder.sectionLabel = (TextView) convertView.findViewById(R.id.section_label);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.SECTION_ID;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.sectionLabel.setText(sectionLabel);
    return convertView;
  }

  @NotNull
  private View displayToggle(View convertView, ViewGroup parent, @NotNull final ToggleRowData rowData) {
    final ViewHolder viewHolder;
    if (convertView == null || !convertView.getTag().equals(RowData.Type.TOGGLE)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_toggle, parent, false);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.toggleSwitch = (Switch) convertView.findViewById(R.id.toggle_switch);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.KEY_VALUE;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.name.setText(rowData.getName());
    viewHolder.toggleSwitch.setChecked(rowData.isActive());
    viewHolder.toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Character pc = CharacterManager.getActiveCharacter();
        if (pc != null) {
          pc.setActionConditionState(rowData.getName(), viewHolder.toggleSwitch.isChecked());
        }
      }
    });
    return convertView;
  }

  private static class ViewHolder {
    RowData.Type type;
    TextView sectionLabel;

    TextView name;
    Switch toggleSwitch;
  }
}
