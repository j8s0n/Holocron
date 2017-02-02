package org.raincitygamers.holocron.ui.pages.skills;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.SkillRating;

import java.util.List;

public class SkillArrayAdapter extends ArrayAdapter<SkillRating> {
  public SkillArrayAdapter(Context context, List<SkillRating> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    SkillRating skillRating = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.skill_list_item, parent, false);
      viewHolder.skillName = (TextView) convertView.findViewById(R.id.skill_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.skillName.setText(skillRating.getSkill().getName());
    viewHolder.skillChar.setText("(" + skillRating.getSkill().getCharacteristic().getAbbreviation() + ")");
    viewHolder.skillRating.setText(skillRating.getRating());
    return  convertView;
  }

  private static class ViewHolder {
    TextView skillName;
    TextView skillChar;
    TextView skillRating;
  }
}
