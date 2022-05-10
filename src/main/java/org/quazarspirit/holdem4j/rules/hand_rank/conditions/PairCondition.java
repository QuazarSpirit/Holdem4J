package org.quazarspirit.holdem4j.rules.hand_rank.conditions;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.card_pile.Hand;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.NullCardPile;

public class PairCondition implements Condition {
    static protected Hand pair = new Hand();
    public static PairCondition isPair() {
        //pair.init(2);
        return new PairCondition();
    }

    public static Hand getPair() {
        return pair;
    }


    @Override
    public boolean evaluate(Facts facts) {
        ICardPile cardPile = facts.get("card_pile");
        if (cardPile.equals(NullCardPile.GetSingleton())) {
            return false;
        }

        final Hand hand = (Hand) cardPile;
        final int handSize = hand.size();

        for(int i = 1; i < handSize; i++) {
            for (int j = i+1; j < handSize; j++) {
                Card card_1 = hand.getCardAt(i);
                Card card_2 = hand.getCardAt(j);
                if (card_1.getRank().equals(card_2.getRank())) {
                    pair.pushCard(card_1);
                    pair.pushCard(card_2);
                    return true;
                }
            }
        }
        return false;
    }
}
