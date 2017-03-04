package org.raincitygamers.holocron.ui.chooser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Specialization;
import org.raincitygamers.holocron.rules.managers.CharacterManager;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

class SpecializationsArrayAdapter extends ArrayAdapter<SpecializationsArrayAdapter.SpecializationRowData> {
  SpecializationsArrayAdapter(Context context, List<SpecializationRowData> objects) {
    super(context, -1, objects);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    final SpecializationRowData specialization = getItem(position);
    final ViewHolder viewHolder;

    // if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_specialization, parent, false);
      viewHolder.specializationName = (TextView) convertView.findViewById(R.id.specialization_name);
      viewHolder.selected = (Switch) convertView.findViewById(R.id.selected);
      viewHolder.selected.setChecked(specialization.isSelected());
      convertView.setTag(viewHolder);
    /*
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    */

    if (specialization != null) {
      viewHolder.specializationName.setText(specialization.getSpecialization().getLongPrettyName());
      viewHolder.selected.setChecked(specialization.isSelected());
      viewHolder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          specialization.selected = isChecked;
          if (isChecked) {
            CharacterManager.getActiveCharacter().addSecondarySpecialization(specialization.getSpecialization());
          }
          else {
            CharacterManager.getActiveCharacter().removeSecondarySpecialization(specialization.getSpecialization());
          }
        }
      });
    }

    return  convertView;
  }

  private static class ViewHolder {
    TextView specializationName;
    Switch selected;
  }

  @Getter
  @AllArgsConstructor(suppressConstructorProperties = true, staticName = "of")
  static class SpecializationRowData {
    private final Specialization specialization;
    @Setter private boolean selected;
  }
}
