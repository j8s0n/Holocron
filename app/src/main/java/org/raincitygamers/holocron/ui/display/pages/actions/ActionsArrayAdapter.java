package org.raincitygamers.holocron.ui.display.pages.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.AttackAction;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.managers.CharacterManager;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.pages.rowdata.AttackActionRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SectionRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.SkillActionRowData;
import org.raincitygamers.holocron.ui.display.pages.rowdata.ToggleRowData;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.raincitygamers.holocron.rules.managers.CharacterManager.getActiveCharacter;

class ActionsArrayAdapter extends ArrayAdapter<RowData> {
  private final FragmentInvalidator invalidator;

  ActionsArrayAdapter(Context context, List<RowData> objects, @NotNull FragmentInvalidator invalidator) {
    super(context, -1, objects);
    this.invalidator = invalidator;
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
        return displayToggle(convertView, parent, (ToggleRowData) rowData);
      case SKILL_ACTION:
        return displaySkillAction(convertView, parent, (SkillActionRowData) rowData);
      case ATTACK_ACTION:
        return displayAttackAction(convertView, parent, (AttackActionRowData) rowData);
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
      viewHolder.type = RowData.Type.TOGGLE;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.name.setText(rowData.getName());
    viewHolder.toggleSwitch.setChecked(rowData.isActive());
    viewHolder.toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getActiveCharacter().setActionConditionState(rowData.getName(), viewHolder.toggleSwitch.isChecked());
        invalidator.invalidate();
      }
    });
    return convertView;
  }

  @NotNull
  private View displaySkillAction(View convertView, @NonNull ViewGroup parent, @NotNull SkillActionRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !convertView.getTag().equals(RowData.Type.SKILL_ACTION)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_skill, parent, false);
      viewHolder.skillName = (TextView) convertView.findViewById(R.id.skill_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      viewHolder.isCareerSkill = (TextView) convertView.findViewById(R.id.career_skill);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.SKILL_ACTION;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Set<String> activeConditions = CharacterManager.getActiveCharacter().getActiveConditions();
    SkillAction skillAction = rowData.getSkillAction();
    DicePool dicePool = DicePool.of(skillAction);
    dicePool.increasePool(skillAction.getPoolBonus(activeConditions));
    viewHolder.skillName.setText(skillAction.getName());
    dicePool.populateLayout(viewHolder.diceLayout, getContext());
    viewHolder.skillChar.setText("");
    viewHolder.skillRating.setText("");
    viewHolder.isCareerSkill.setText("");
    return convertView;
  }

  @NotNull
  private View displayAttackAction(View convertView, @NotNull ViewGroup parent, @NotNull AttackActionRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !convertView.getTag().equals(RowData.Type.SKILL_ACTION)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_attack, parent, false);
      viewHolder.skillName = (TextView) convertView.findViewById(R.id.skill_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      viewHolder.isCareerSkill = (TextView) convertView.findViewById(R.id.career_skill);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.SKILL_ACTION;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Set<String> activeConditions = CharacterManager.getActiveCharacter().getActiveConditions();
    AttackAction attackAction = rowData.getAttackAction();
    DicePool dicePool = DicePool.of(attackAction);
    dicePool.increasePool(attackAction.getPoolBonus(activeConditions));
    viewHolder.skillName.setText(attackAction.getName());
    dicePool.populateLayout(viewHolder.diceLayout, getContext());
    viewHolder.skillChar.setText("");
    viewHolder.skillRating.setText("");
    viewHolder.isCareerSkill.setText("");
    return convertView;
  }

  private static class ViewHolder {
    RowData.Type type;

    // Section ID
    TextView sectionLabel;

    // Toggle
    TextView name;
    Switch toggleSwitch;

    // Skill Action
    TextView skillName;
    TextView skillChar;
    LinearLayout diceLayout;
    TextView skillRating;
    TextView isCareerSkill;

    // Attack Action
  }
}
