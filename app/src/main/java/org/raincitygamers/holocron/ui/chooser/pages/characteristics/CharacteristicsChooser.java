package org.raincitygamers.holocron.ui.chooser.pages.characteristics;

import org.raincitygamers.holocron.rules.abilities.Characteristic;
import org.raincitygamers.holocron.rules.character.Character;
import org.raincitygamers.holocron.ui.chooser.pages.scoregroup.ScoreGroupChooser;
import org.raincitygamers.holocron.ui.chooser.pages.scoregroup.ScoreRating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CharacteristicsChooser extends ScoreGroupChooser {
  public CharacteristicsChooser() {
  }

  @Override
  public String getTitle() {
    return "Choose Characteristics";
  }

  @Override
  public void onPause() {
    super.onPause();

    Character pc = getActiveCharacter();
    for (Map.Entry<String, Integer> entry : getScores().entrySet()) {
      pc.setCharacteristicScore(Characteristic.of(entry.getKey()), entry.getValue());
    }
  }

  @Override
  protected Collection<ScoreRating> getScoreRatings() {
    List<ScoreRating> scores = new ArrayList<>();
    Character pc = getActiveCharacter();
    for (Characteristic characteristic : Characteristic.values()) {
      scores.add(ScoreRating.of(characteristic.toString(), pc.getCharacteristicScore(characteristic), 1, 6));
    }

    return scores;
  }
}
