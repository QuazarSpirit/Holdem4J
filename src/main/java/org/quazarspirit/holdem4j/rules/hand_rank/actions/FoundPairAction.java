package org.quazarspirit.holdem4j.rules.hand_rank.actions;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;
import org.quazarspirit.holdem4j.game_logic.card_pile.Hand;
import org.quazarspirit.holdem4j.rules.hand_rank.conditions.PairCondition;

public class FoundPairAction implements Action {
    public static FoundPairAction addPair() {
        return new FoundPairAction();
    }

    @Override
    public void execute(Facts facts) {
        Hand pair = PairCondition.getPair();
        facts.put("first_pair", pair);
    }
}
