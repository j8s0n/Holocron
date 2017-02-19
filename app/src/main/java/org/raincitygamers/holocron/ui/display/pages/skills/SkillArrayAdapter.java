package org.raincitygamers.holocron.ui.display.pages.skills;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.traits.DicePool;

import java.util.List;
import java.util.Locale;

public class SkillArrayAdapter extends ArrayAdapter<DicePool> {
  public SkillArrayAdapter(Context context, List<DicePool> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    DicePool dicePool = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_skill, parent, false);
      viewHolder.skillName = (TextView) convertView.findViewById(R.id.skill_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      viewHolder.isCareerSkill = (TextView) convertView.findViewById(R.id.career_skill);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.skillName.setText(dicePool.getSkill().getName());
    viewHolder.skillChar.setText("(" + dicePool.getSkill().getCharacteristic().getAbbreviation() + ")");
    viewHolder.isCareerSkill.setVisibility(dicePool.isCareerSkill() ? View.VISIBLE : View.INVISIBLE);
    dicePool.populateLayout(viewHolder.diceLayout, getContext());

    viewHolder.skillRating.setText(String.format(Locale.US, "(%d)", dicePool.getRating()));
    return  convertView;
  }

  private static class ViewHolder {
    TextView skillName;
    TextView skillChar;
    LinearLayout diceLayout;
    TextView skillRating;
    TextView  isCareerSkill;
  }
}
