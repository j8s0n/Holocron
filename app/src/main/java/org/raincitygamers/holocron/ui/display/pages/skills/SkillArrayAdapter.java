package org.raincitygamers.holocron.ui.display.pages.skills;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    int layoutWidth = 0;
    viewHolder.skillName.setText(skillRating.getSkill().getName());
    viewHolder.skillChar.setText("(" + skillRating.getSkill().getCharacteristic().getAbbreviation() + ")");
    viewHolder.isCareerSkill.setVisibility(skillRating.isCareerSkill() ? View.VISIBLE : View.INVISIBLE);
    viewHolder.diceLayout.removeAllViews();
    for (int i = 0; i < skillRating.getProficiencyDiceCount(); i++) {
      ImageView die = new ImageView(getContext());
      die.setImageResource(R.drawable.ic_proficiency_die);
      viewHolder.diceLayout.addView(die);
      layoutWidth += die.getWidth();
    }

    for (int i = 0; i < skillRating.getAbilityDiceCount(); i++) {
      ImageView die = new ImageView(getContext());
      die.setImageResource(R.drawable.ic_ability_die);
      viewHolder.diceLayout.addView(die);
      layoutWidth += die.getWidth();
    }

    ViewGroup.LayoutParams params = viewHolder.diceLayout.getLayoutParams();
    params.width = layoutWidth;
    viewHolder.diceLayout.setLayoutParams(params);
    viewHolder.skillRating.setText(skillRating.getRating());
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
