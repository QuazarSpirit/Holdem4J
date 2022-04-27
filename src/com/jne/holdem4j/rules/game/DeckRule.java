package com.jne.holdem4j.rules.game;

import com.jne.business_rule_engine.ICheckable;
import com.jne.business_rule_engine.IRule;
import com.jne.holdem4j.game_logic.Deck;

import java.util.List;

public class DeckRule implements IRule {

    @Override
    public boolean shouldRun(ICheckable checkable) {
        boolean isDeck = checkable instanceof Deck;

        return isDeck;
    }

    @Override
    public List<String> runRule(ICheckable checkable) {
        Deck deck = (Deck) checkable;


        System.out.println(deck.getAttribute("size"));

        return null;
    }
}
