package org.raincitygamers.holocron.ui.chooser.pages.force;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.rules.managers.ForcePowerManager;
import org.raincitygamers.holocron.rules.traits.Ability;
import org.raincitygamers.holocron.rules.traits.ForcePowerUpgrade;
import org.raincitygamers.holocron.ui.chooser.ChooserBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForceChooser extends ChooserBase {
  private static final String LOG_TAG = ForceChooser.class.getSimpleName();

  private List<String> forcePowerNames = new ArrayList<>();
  private Map<String, List<Integer>> powersTaken;
  private String chosenPower;
  private int forceRating;

  private Spinner knownUpgradesSpinner;
  private Spinner availableUpgradesSpinner;

  public ForceChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose Force Powers";
  }

  @Override
  public void onPause() {
    super.onPause();
    Character pc = getActiveCharacter();
    pc.setForceRating(forceRating);
  }

  @Override
  public void onResume() {
    super.onResume();
    Character pc = getActiveCharacter();
    forcePowerNames.clear();
    forcePowerNames.addAll(ForcePowerManager.getPowerNames());
    forceRating = pc.getForceRating();
    powersTaken = pc.getForcePowers();

    setUpForcePowerButtons();
    updateForceRating(forceRating);

    buildForcePowersSpinner();
    setUpPowerChoiceButtons();
  }

  private void setUpForcePowerButtons() {
    showHideWidgets(forceRating > 0);
    TextView decreaseRating = (TextView) getView().findViewById(R.id.score_rating_down_button);
    TextView increaseRating = (TextView) getView().findViewById(R.id.score_rating_up_button);
    decreaseRating.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (forceRating > 0) {
          updateForceRating(--forceRating);
        }

        if (forceRating == 0) {
          showHideWidgets(false);
        }
      }
    });

    increaseRating.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateForceRating(++forceRating);
        if (forceRating == 1) {
          showHideWidgets(true);
        }
      }
    });
  }

  private void setUpPowerChoiceButtons() {

    Button addUpgrade = (Button) getView().findViewById(R.id.add_power_upgrade);
    addUpgrade.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (availableUpgradesSpinner.getAdapter().getCount() == 0) {
          return;
        }

        SpinnerEntry entry = (SpinnerEntry) availableUpgradesSpinner.getSelectedItem();
        powersTaken.get(chosenPower).add(entry.index);
        buildUpgradeSpinners();
      }
    });

    Button removeUpgrade = (Button) getView().findViewById(R.id.remove_power_upgrade);
    removeUpgrade.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (knownUpgradesSpinner.getAdapter().getCount() == 0) {
          return;
        }

        SpinnerEntry entry = (SpinnerEntry) knownUpgradesSpinner.getSelectedItem();
        int index = powersTaken.get(chosenPower).indexOf(entry.index);
        powersTaken.get(chosenPower).remove(index);
        buildUpgradeSpinners();
      }
    });

  }

  private void updateForceRating(int forceRating) {
    TextView forceRatingEntry = (TextView) getView().findViewById(R.id.score_rating_entry);
    forceRatingEntry.setText(String.format("%d", forceRating));
  }

  private void showHideWidgets(boolean show) {
    int visibility = show ? View.VISIBLE : View.INVISIBLE;
    getView().findViewById(R.id.force_power_label).setVisibility(visibility);
    getView().findViewById(R.id.force_power_spinner).setVisibility(visibility);
    getView().findViewById(R.id.available_upgrades_label).setVisibility(visibility);
    getView().findViewById(R.id.available_spinner).setVisibility(visibility);
    getView().findViewById(R.id.add_power_upgrade).setVisibility(visibility);
    getView().findViewById(R.id.acquired_upgrades_label).setVisibility(visibility);
    getView().findViewById(R.id.known_spinner).setVisibility(visibility);
    getView().findViewById(R.id.remove_power_upgrade).setVisibility(visibility);
  }

  private void buildForcePowersSpinner() {
    Spinner forcePowersSpinner = (Spinner) getView().findViewById(R.id.force_power_spinner);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                                                           forcePowerNames);
    arrayAdapter.notifyDataSetChanged();
    forcePowersSpinner.setAdapter(arrayAdapter);

    forcePowersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenPower = forcePowerNames.get(position);
        buildUpgradeSpinners();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void buildUpgradeSpinners() {
    knownUpgradesSpinner = (Spinner) getView().findViewById(R.id.known_spinner);
    availableUpgradesSpinner = (Spinner) getView().findViewById(R.id.available_spinner);

    List<SpinnerEntry> knownUpgrades = new ArrayList<>();
    List<SpinnerEntry> availableUpgrades = new ArrayList<>();
    List<ForcePowerUpgrade> upgradesList = ForcePowerManager.getList(chosenPower);
    List<Integer> upgradesKnown = powersTaken.get(chosenPower);
    if (upgradesKnown == null) {
      powersTaken.put(chosenPower, new ArrayList<Integer>());
      upgradesKnown = powersTaken.get(chosenPower);
    }

    for (int i = 0; i < upgradesList.size(); i++) {
      if (upgradesKnown.contains(i)) {
        knownUpgrades.add(new SpinnerEntry(upgradesList.get(i), i));
      }
      else {
        availableUpgrades.add(new SpinnerEntry(upgradesList.get(i), i));
      }
    }

    ArrayAdapter<SpinnerEntry> knownUpgradesAdapter = new ArrayAdapter<>(getActivity(),
                                                                         android.R.layout.simple_dropdown_item_1line,
                                                                         knownUpgrades);
    ArrayAdapter<SpinnerEntry> availableUpgradesAdapter = new ArrayAdapter<>(getActivity(),
                                                                             android.R.layout
                                                                                 .simple_dropdown_item_1line,
                                                                             availableUpgrades);

    knownUpgradesSpinner.setAdapter(knownUpgradesAdapter);
    availableUpgradesSpinner.setAdapter(availableUpgradesAdapter);
    knownUpgradesAdapter.notifyDataSetChanged();
    availableUpgradesAdapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.choose_force_powers, container, false);

    return result;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }

  private static class SpinnerEntry {
    final String display;
    final int index;
    final int row;
    final int column;

    public SpinnerEntry(@NotNull Ability ability, int index) {
      this.index = index;
      this.row = ability.getRow();
      this.column = ability.getColumn();
      this.display = String.format("%s (Row %d, Col %d)", ability.getName(), row, column);
    }

    @Override
    public String toString() {
      return display;
    }
  }
}
