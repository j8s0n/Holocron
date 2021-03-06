package org.raincitygamers.holocron.ui.display;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.AttackAction;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.character.InventoryItem;
import org.raincitygamers.holocron.rules.character.SkillAction;
import org.raincitygamers.holocron.rules.traits.Ability;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.rules.traits.DicePool.BonusType;
import org.raincitygamers.holocron.ui.FragmentInvalidator;
import org.raincitygamers.holocron.ui.display.rowdata.AbilityRowData;
import org.raincitygamers.holocron.ui.display.rowdata.AdderRowData;
import org.raincitygamers.holocron.ui.display.rowdata.AttackActionRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ButtonRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ConditionEditorRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ConditionalBonusRowData;
import org.raincitygamers.holocron.ui.display.rowdata.DicePoolRowData;
import org.raincitygamers.holocron.ui.display.rowdata.InventoryItemRowData;
import org.raincitygamers.holocron.ui.display.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.display.rowdata.RowData;
import org.raincitygamers.holocron.ui.display.rowdata.ScoreRowData;
import org.raincitygamers.holocron.ui.display.rowdata.SectionRowData;
import org.raincitygamers.holocron.ui.display.rowdata.SkillActionRowData;
import org.raincitygamers.holocron.ui.display.rowdata.SpinnerRowData;
import org.raincitygamers.holocron.ui.display.rowdata.TextEditorRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ThresholdRowData;
import org.raincitygamers.holocron.ui.display.rowdata.ToggleRowData;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.raincitygamers.holocron.rules.managers.CharacterManager.getActiveCharacter;
import static org.raincitygamers.holocron.ui.display.SkillActionEditorActivity.ACTION_TO_EDIT;
import static org.raincitygamers.holocron.ui.display.SkillActionEditorActivity.ACTION_TYPE;
import static org.raincitygamers.holocron.ui.display.rowdata.RowData.Type.SKILL_ACTION;

public class DisplayArrayAdapter extends ArrayAdapter<RowData> {

  private final FragmentInvalidator invalidator;
  private static final float IGNORED_ENCUMBRANCE = 0.2f;
  private TextView encumbrance;

  public DisplayArrayAdapter(Context context, List<RowData> objects, @Nullable FragmentInvalidator invalidator) {
    super(context, -1, objects);
    this.invalidator = invalidator;
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    RowData rowData = getItem(position);
    if (rowData != null) {
      switch (rowData.getType()) {
      case ABILITY:
        return displayAbility(convertView, parent, ((AbilityRowData) rowData).getAbility());
      case ADDER:
        return displayAdder(convertView, parent, (AdderRowData) rowData);
      case ATTACK_ACTION:
        return displayAttackAction(convertView, parent, (AttackActionRowData) rowData);
      case BUTTON:
        return displayButton(convertView, parent, (ButtonRowData) rowData);
      case CONDITIONAL_BONUS:
        return displayConditionalBonus(convertView, parent, ((ConditionalBonusRowData) rowData));
      case DICE_POOL:
        return displayDicePool(convertView, parent, ((DicePoolRowData) rowData).getDicePool());
      case EDIT_CONDITION:
        return displayEditCondition(convertView, parent, (ConditionEditorRowData) rowData);
      case INVENTORY:
        return displayInventory(convertView, parent, ((InventoryItemRowData) rowData).getItem());
      case KEY_VALUE:
        return displayKeyValuePair(convertView, parent, (KeyValueRowData) rowData);
      case SCORE:
        return displayScore(convertView, parent, (ScoreRowData) rowData);
      case SECTION_ID:
        return displaySection(convertView, parent, (SectionRowData) rowData);
      case SKILL_ACTION:
        return displaySkillAction(convertView, parent, (SkillActionRowData) rowData);
      case SPINNER:
        return displaySpinner(convertView, parent, (SpinnerRowData) rowData);
      case TEXT_EDITOR:
        return displayTextEdit(convertView, parent, (TextEditorRowData) rowData);
      case THRESHOLD:
        return displayThreshold(convertView, parent, (ThresholdRowData) rowData);
      case TOGGLE:
        return displayToggle(convertView, parent, (ToggleRowData) rowData);
      default:
        throw new IllegalStateException(String.format(Locale.US, "Row type %s is not valid here.", rowData.getType()));
      }
    }

    throw new IllegalStateException(String.format(Locale.US, "No item at position %d.", position));
  }

  @NotNull
  private View displayAbility(View convertView, @NotNull ViewGroup parent, @NotNull Ability ability) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.ABILITY)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_ability, parent, false);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.tier = (TextView) convertView.findViewById(R.id.tier);
      viewHolder.description = (TextView) convertView.findViewById(R.id.description);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.ABILITY;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.name.setText(ability.getName());
    viewHolder.description.setText(ability.getDescription());
    viewHolder.tier.setText(String.format(Locale.US, "%d", ability.getTier()));
    return convertView;
  }

  @NotNull
  private View displayAdder(View convertView, @NotNull ViewGroup parent, @NotNull final AdderRowData adder) {
    final ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.ADDER)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_adder, parent, false);
      viewHolder.itemEntry = (EditText) convertView.findViewById(R.id.item_entry);
      viewHolder.addButton = (TextView) convertView.findViewById(R.id.add_button);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.ADDER;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.addButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        adder.getAddPerformer().performAdd(viewHolder.itemEntry.getText().toString());
      }
    });

    return convertView;
  }

  @NotNull
  private View displayAttackAction(View convertView, @NotNull ViewGroup parent, @NotNull AttackActionRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.ATTACK_ACTION)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_attack, parent, false);
      viewHolder.entryName = (TextView) convertView.findViewById(R.id.entry_name);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.damageValue = (TextView) convertView.findViewById(R.id.damage_value);
      viewHolder.criticalValue = (TextView) convertView.findViewById(R.id.crit_value);
      viewHolder.rangeValue = (TextView) convertView.findViewById(R.id.range_value);
      viewHolder.attackText = (TextView) convertView.findViewById(R.id.attack_text);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.ATTACK_ACTION;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Set<String> activeConditions = getActiveCharacter().getActiveConditions();
    final AttackAction attackAction = rowData.getAttackAction();
    DicePool dicePool = DicePool.of(attackAction);
    dicePool.increasePool(attackAction.getPoolBonus(activeConditions));
    viewHolder.entryName.setText(attackAction.getName());
    dicePool.populateLayout(viewHolder.diceLayout, getContext());

    int damage = attackAction.getDamage() + dicePool.getBonus(BonusType.DAMAGE);
    int critical = attackAction.getCritical();
    String criticalString;
    if (critical < 0) {
      criticalString = "-";
    }
    else {
      critical = Math.max(1, critical - dicePool.getBonus(BonusType.CRITICAL));
      criticalString = String.format(Locale.US, "%d", critical);
    }

    viewHolder.damageValue.setText(String.format(Locale.US, "%d", damage));
    viewHolder.criticalValue.setText(criticalString);
    viewHolder.rangeValue.setText(attackAction.getRange().getName());
    viewHolder.attackText.setText(attackAction.getText());
    convertView.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Intent intent = new Intent(getContext(), SkillActionEditorActivity.class);
        intent.putExtra(ACTION_TYPE, SkillActionEditorActivity.ActionType.ATTACK);
        intent.putExtra(ACTION_TO_EDIT, attackAction.getName());
        getContext().startActivity(intent);

        if (invalidator != null) {
          invalidator.invalidate();
        }

        return true;
      }
    });

    return convertView;
  }

  @NotNull
  private View displayButton(View convertView, @NotNull ViewGroup parent, @NotNull ButtonRowData button) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.BUTTON)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_button, parent, false);
      viewHolder.button = (Button) convertView.findViewById(R.id.button);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.BUTTON;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.button.setText(button.getButtonText());
    viewHolder.button.setOnClickListener(button.getOnClickListener());
    return convertView;
  }

  @NotNull
  private View displayConditionalBonus(View convertView, @NonNull ViewGroup parent,
                                       @NotNull final ConditionalBonusRowData rowData) {
    final ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.CONDITIONAL_BONUS)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_skill, parent, false);
      viewHolder.entryName = (TextView) convertView.findViewById(R.id.entry_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      viewHolder.isCareerSkill = (TextView) convertView.findViewById(R.id.career_skill);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.CONDITIONAL_BONUS;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.entryName.setText(rowData.getCondition());
    Set<BonusType> extras = new HashSet<>();
    extras.add(BonusType.SKILL_RANK);
    extras.add(BonusType.UPGRADE);
    extras.add(BonusType.CRITICAL);
    extras.add(BonusType.DAMAGE);
    DicePool.populateLayout(viewHolder.diceLayout, getContext(), rowData.getBonuses(), extras);

    viewHolder.skillChar.setText("");
    viewHolder.skillRating.setText("");
    viewHolder.isCareerSkill.setText("");
    convertView.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Intent intent = new Intent(getContext(), BonusEditorActivity.class);
        intent.putExtra(ACTION_TO_EDIT, rowData.getSkillActionName());
        intent.putExtra(BonusEditorActivity.CONDITION_NAME, rowData.getCondition());
        intent.putExtra(ACTION_TYPE, rowData.getActionType());

        Context context = getContext();
        if (context instanceof SkillActionEditorActivity) {
          SkillActionEditorActivity activity = (SkillActionEditorActivity) context;
          activity.startActivityForResult(intent, SkillActionEditorActivity.BONUS_EDITOR_ACTIVITY);
        }

        if (invalidator != null) {
          invalidator.invalidate();
        }

        return true;
      }
    });
    return convertView;
  }

  @NotNull
  private View displayDicePool(View convertView, @NotNull ViewGroup parent, @NotNull DicePool dicePool) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.DICE_POOL)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_skill, parent, false);
      viewHolder.poolName = (TextView) convertView.findViewById(R.id.entry_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      viewHolder.isCareerSkill = (TextView) convertView.findViewById(R.id.career_skill);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.DICE_POOL;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.poolName.setText(dicePool.getSkill().getName());
    viewHolder.skillChar.setText("(" + dicePool.getSkill().getCharacteristic().getAbbreviation() + ")");
    viewHolder.isCareerSkill.setVisibility(dicePool.isCareerSkill() ? View.VISIBLE : View.INVISIBLE);
    dicePool.populateLayout(viewHolder.diceLayout, getContext());

    viewHolder.skillRating.setText(String.format(Locale.US, "(%d)", dicePool.getRating()));
    return convertView;
  }

  @NotNull
  private View displayInventory(View convertView, @NotNull final ViewGroup parent, @NotNull final InventoryItem item) {
    final ViewHolder viewHolder;

    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.INVENTORY)) {
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
      viewHolder.type = RowData.Type.INVENTORY;
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

    setInventoryClickHandlers(viewHolder, item);
    return convertView;
  }

  @NotNull
  private View displayKeyValuePair(View convertView, @NotNull ViewGroup parent, @NotNull KeyValueRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.KEY_VALUE)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_key_value, parent, false);
      viewHolder.key = (TextView) convertView.findViewById(R.id.key);
      viewHolder.value = (TextView) convertView.findViewById(R.id.value);
      viewHolder.unitsIcon = (ImageView) convertView.findViewById(R.id.units_icon);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.KEY_VALUE;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.key.setText(rowData.getPair().getKey());
    viewHolder.value.setText(rowData.getPair().getValue());
    if (rowData.getImageId() != 0) {
      viewHolder.unitsIcon.setImageResource(rowData.getImageId());
      viewHolder.unitsIcon.setVisibility(View.VISIBLE);
    }
    else {
      viewHolder.unitsIcon.setVisibility(View.GONE);
    }

    convertView.setOnLongClickListener(rowData.getLongClickListener());

    if (rowData.getPair().getKey().equals("Encumbrance")) {
      encumbrance = viewHolder.value;
    }

    return convertView;
  }

  @NotNull
  private View displayEditCondition(View convertView, @NotNull ViewGroup parent,
                                    @NotNull ConditionEditorRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.EDIT_CONDITION)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_remove_action, parent, false);
      viewHolder.conditionName = (TextView) convertView.findViewById(R.id.item_entry);
      viewHolder.removeButton = (TextView) convertView.findViewById(R.id.remove_button);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.EDIT_CONDITION;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.conditionName.setText(rowData.getCondition());
    viewHolder.removeButton.setOnClickListener(rowData.getOnClickListener());
    return convertView;
  }

  @NotNull
  private View displayScore(View convertView, @NotNull ViewGroup parent, @NotNull final ScoreRowData rowData) {
    final ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.SCORE)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_choose_score, parent, false);
      viewHolder.scoreName = (TextView) convertView.findViewById(R.id.score_rating_label);
      viewHolder.minusButton = (TextView) convertView.findViewById(R.id.score_rating_down_button);
      viewHolder.scoreValue = (TextView) convertView.findViewById(R.id.score_rating_entry);
      viewHolder.plusButton = (TextView) convertView.findViewById(R.id.score_rating_up_button);
      viewHolder.bonusIcon = (ImageView) convertView.findViewById(R.id.score_rating_image);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.SCORE;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.scoreName.setText(rowData.getBonusType().getName());
    viewHolder.scoreValue.setText(String.format(Locale.getDefault(), "%d", rowData.getCount()));
    viewHolder.bonusIcon.setImageResource(rowData.getBonusType().getResourceId());
    viewHolder.minusButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int score = Integer.parseInt(String.valueOf(viewHolder.scoreValue.getText()));
        if (score > 0) {
          score--;
        }

        viewHolder.scoreValue.setText(String.format(Locale.getDefault(), "%d", score));
        rowData.getWatcher().valueUpdated(rowData.getBonusType(), score);
        rowData.setCount(score);
      }
    });

    viewHolder.plusButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int score = Integer.parseInt(String.valueOf(viewHolder.scoreValue.getText()));
        if (score < 10) {
          score++;
        }

        viewHolder.scoreValue.setText(String.format(Locale.getDefault(), "%d", score));
        rowData.getWatcher().valueUpdated(rowData.getBonusType(), score);
        rowData.setCount(score);
      }
    });

    return convertView;
  }

  @NotNull
  private View displaySection(View convertView, @NotNull ViewGroup parent, @NotNull final SectionRowData sectionRowData) {
    ViewHolder viewHolder;
    final String sectionLabel = sectionRowData.getSectionId();
    final String pageName = sectionRowData.getContainerPage();
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.SECTION_ID)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_section, parent, false);
      viewHolder.sectionLabel = (TextView) convertView.findViewById(R.id.section_label);
      viewHolder.expandArrow = (ImageView) convertView.findViewById(R.id.expand_arrow);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.SECTION_ID;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.sectionLabel.setText(sectionLabel);
    viewHolder.expandArrow.setImageResource(sectionRowData.isCollapsed() ? R.drawable.ic_expand_arrow
                                                                         : R.drawable.ic_collapse_arrow);

    convertView.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        sectionRowData.setCollapsed(!sectionRowData.isCollapsed());
        getActiveCharacter().toggleSection(pageName, sectionLabel);
        if (invalidator != null) {
          invalidator.invalidate();
        }

        return true;
      }
    });

    return convertView;
  }

  @NotNull
  private View displaySkillAction(View convertView, @NonNull ViewGroup parent, @NotNull SkillActionRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(SKILL_ACTION)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_skill, parent, false);
      viewHolder.entryName = (TextView) convertView.findViewById(R.id.entry_name);
      viewHolder.skillChar = (TextView) convertView.findViewById(R.id.skill_char);
      viewHolder.diceLayout = (LinearLayout) convertView.findViewById(R.id.dice_layout);
      viewHolder.skillRating = (TextView) convertView.findViewById(R.id.skill_rating);
      viewHolder.isCareerSkill = (TextView) convertView.findViewById(R.id.career_skill);
      convertView.setTag(viewHolder);
      viewHolder.type = SKILL_ACTION;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Set<String> activeConditions = getActiveCharacter().getActiveConditions();
    final SkillAction skillAction = rowData.getSkillAction();
    DicePool dicePool = DicePool.of(skillAction);
    dicePool.increasePool(skillAction.getPoolBonus(activeConditions));
    viewHolder.entryName.setText(skillAction.getName());
    dicePool.populateLayout(viewHolder.diceLayout, getContext());
    viewHolder.skillChar.setText("");
    viewHolder.skillRating.setText("");
    viewHolder.isCareerSkill.setText("");
    convertView.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Intent intent = new Intent(getContext(), SkillActionEditorActivity.class);
        intent.putExtra(ACTION_TO_EDIT, skillAction.getName());
        intent.putExtra(ACTION_TYPE, SkillActionEditorActivity.ActionType.SKILL);
        getContext().startActivity(intent);

        if (invalidator != null) {
          invalidator.invalidate();
        }

        return true;
      }
    });

    return convertView;
  }

  @NotNull
  private View displaySpinner(View convertView, @NotNull ViewGroup parent, @NotNull final SpinnerRowData rowData) {
    final ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.SPINNER)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_spinner, parent, false);
      viewHolder.spinner = (Spinner) convertView.findViewById(R.id.spinner);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.SPINNER;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                                                           rowData.getContent());
    viewHolder.spinner.setAdapter(arrayAdapter);
    viewHolder.spinner.setSelection(rowData.getSelectedItem());

    viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        rowData.getSpinnerWatcher().itemSelected(rowData.getContent().get(position));
        rowData.setSelectedItem(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
      }
    });

    return convertView;
  }

  @NotNull
  private View displayTextEdit(View convertView, @NotNull ViewGroup parent, @NotNull final TextEditorRowData rowData) {
    final ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.TEXT_EDITOR)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_text_editor, parent, false);
      viewHolder.textEditor = (EditText) convertView.findViewById(R.id.text_editor);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.TEXT_EDITOR;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.textEditor.setText(rowData.getTextValue());
    viewHolder.textEditor.setHint(rowData.getHint());
    viewHolder.textEditor.setInputType(rowData.getInputType());
    viewHolder.textEditor.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        rowData.setTextValue(s.toString());
        rowData.getWatcher().valueUpdated(rowData.getTextValue());
      }
    });

    return convertView;
  }


  @NotNull
  private View displayThreshold(View convertView, @NotNull ViewGroup parent, @NotNull final ThresholdRowData rowData) {
    ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.THRESHOLD)) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.list_item_key_value, parent, false);
      viewHolder.key = (TextView) convertView.findViewById(R.id.key);
      viewHolder.value = (TextView) convertView.findViewById(R.id.value);
      convertView.setTag(viewHolder);
      viewHolder.type = RowData.Type.THRESHOLD;
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.key.setText(rowData.getPair().getKey());
    viewHolder.value.setText(rowData.getPair().getValue());
    final Character pc = getActiveCharacter();
    viewHolder.value.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          int adjustment = (event.getX() > v.getWidth() / 2.0f) ? -1 : 1;
          if (rowData.getThreshold().equals(ThresholdRowData.STRAIN)) {
            pc.setStrain(pc.getStrain() + adjustment);
          }
          else if (rowData.getThreshold().equals(ThresholdRowData.WOUNDS)) {
            pc.setWounds(pc.getWounds() + adjustment);
          }

          refreshPage();
        }

        return true;
      }
    });

    viewHolder.key.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (rowData.getThreshold().equals(ThresholdRowData.STRAIN)) {
          pc.setStrain(0);
        }
        else if (rowData.getThreshold().equals(ThresholdRowData.WOUNDS)) {
          pc.setWounds(0);
        }

        refreshPage();
        return true;
      }
    });

    return convertView;
  }

  @NotNull
  private View displayToggle(View convertView, @NotNull ViewGroup parent, @NotNull final ToggleRowData rowData) {
    final ViewHolder viewHolder;
    if (convertView == null || !((ViewHolder) convertView.getTag()).type.equals(RowData.Type.TOGGLE)) {
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
        getActiveCharacter().setActionConditionState(rowData.getName(),
                                                     viewHolder.toggleSwitch.isChecked());
        refreshPage();
      }
    });
    return convertView;
  }

  private void setInventoryClickHandlers(@NotNull final ViewHolder viewHolder, @NotNull final InventoryItem item) {

    viewHolder.countEncumbrance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        item.setCountEncumbrance(isChecked);
        viewHolder.encumbrance.setAlpha(isChecked ? 1.0f : IGNORED_ENCUMBRANCE);
        updateEncumbrance();
      }
    });

    viewHolder.quantity.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        item.setQuantity(Math.max(0, item.getQuantity() - 1));
        viewHolder.quantity.setText(String.format(Locale.US, "(%d)", item.getQuantity()));
        updateEncumbrance();
        return true;
      }
    });

    viewHolder.quantity.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        item.setQuantity(item.getQuantity() + 1);
        viewHolder.quantity.setText(String.format(Locale.US, "(%d)", item.getQuantity()));
        updateEncumbrance();
      }
    });

    viewHolder.name.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Intent intent = new Intent(getContext(), InventoryEditorActivity.class);
        int index = getActiveCharacter().getInventory().indexOf(item);
        intent.putExtra(InventoryEditorActivity.INVENTORY_ITEM_TO_EDIT, index);
        getContext().startActivity(intent);

        return true;
      }
    });
  }

  private void updateEncumbrance() {
    if (encumbrance != null) {
      Character pc = getActiveCharacter();
      encumbrance.setText(String.format(Locale.US, "%d / %d", pc.getEncumbrance(), pc.getEncumbranceThreshold()));
    }
  }

  private void refreshPage() {
    if (invalidator != null) {
      invalidator.invalidate();
    }
  }

  private static class ViewHolder {
    RowData.Type type;

    // Generic
    TextView name;
    TextView description;

    // Attack Action
    TextView damageValue;
    TextView criticalValue;
    TextView rangeValue;
    TextView attackText;

    // Ability
    TextView tier;

    // Adder
    EditText itemEntry;
    TextView addButton;

    // Button
    Button button;

    // Condition Editor
    TextView conditionName;
    TextView removeButton;

    // Dice Pool
    TextView poolName;

    // Inventory
    TextView quantity;
    TextView location;
    TextView encumbrance;
    CheckBox countEncumbrance;

    // Key Value Pair
    TextView key;
    TextView value;
    ImageView unitsIcon;

    // Score
    TextView scoreName;
    TextView minusButton;
    TextView scoreValue;
    TextView plusButton;
    ImageView bonusIcon;

    // Section ID
    TextView sectionLabel;
    ImageView expandArrow;

    // Skill Action
    TextView entryName;
    TextView skillChar;
    LinearLayout diceLayout;
    TextView skillRating;
    TextView isCareerSkill;

    // Spinner
    Spinner spinner;

    // Text Editor
    EditText textEditor;

    // Toggle
    Switch toggleSwitch;
  }
}
