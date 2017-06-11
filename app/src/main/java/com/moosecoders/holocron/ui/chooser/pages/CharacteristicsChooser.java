package com.moosecoders.holocron.ui.chooser.pages;

import com.moosecoders.holocron.rules.character.Character;
import com.moosecoders.holocron.rules.traits.Characteristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CharacteristicsChooser extends ScoreGroupChooser {
  private static final String LOG_TAG = CharacteristicsChooser.class.getSimpleName();

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
      scores.add(ScoreRating.of(characteristic.toString(), pc.getCharacteristicScore(characteristic), 1, 6, false));
    }

    return scores;
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }
}
