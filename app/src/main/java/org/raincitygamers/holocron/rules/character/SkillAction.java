package org.raincitygamers.holocron.rules.character;

import org.raincitygamers.holocron.rules.traits.Characteristic;
import org.raincitygamers.holocron.rules.traits.DicePool;
import org.raincitygamers.holocron.rules.traits.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(suppressConstructorProperties = true, staticName = "of")
public class SkillAction {
  private final String name;
  private final Characteristic characteristic;
  private final Skill skill;
  private List<Map<String, Map<DicePool.BonusType, Integer>>> bonuses = new ArrayList<>();
}
