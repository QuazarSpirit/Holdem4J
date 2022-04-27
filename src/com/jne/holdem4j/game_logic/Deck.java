package com.jne.holdem4j.game_logic;
import com.jne.business_rule_engine.Checkable;
import com.jne.business_rule_engine.IRule;

import java.util.ArrayList;
import java.util.Random;

public class Deck extends Checkable {
    final protected ArrayList<Card> _cards = new ArrayList<Card>();
    public Deck() {
        super();
        _init();
    }
    private void _init() {
        _cards.clear();

        for(char color:Card.COLORS.toCharArray()) {
            for(char rank:Card.RANKS.toCharArray()) {
                Card c = new Card(("" + rank + color));
                _cards.add(c);
            }
        }
    }
    private boolean _contains(Card cardToCheck) {
        for (Card card:_cards) {
            if (card.getValue().equals(cardToCheck.getValue())) {
                return true;
            }
        }
        return false;
    }
    public Deck shuffle() {
        Deck initialDeck = this;
        Deck shuffledDeck = new Deck();
        shuffledDeck.clear();


        Random r = new Random();
        while (! initialDeck.isEmpty()) {
            int index = 0;
            if (initialDeck.size() > 1) {
                index = r.nextInt(initialDeck.size() -1);
            }
            Card pickedCard = initialDeck.pick(index);
            shuffledDeck.pushCard(pickedCard);
        }

        return shuffledDeck;
    }
    public String asString() {
        StringBuilder deckAsString = new StringBuilder();
        for (Object o: _cards.toArray()) {
            Card card = (Card) o;
            deckAsString.append(card.getValue()).append("/");
        }

        deckAsString = new StringBuilder(deckAsString.substring(0, deckAsString.length() - 1));
        return deckAsString.toString();
    }
    public Card pick(int index) {
        Card card = getCardAt(index);
        _cards.remove(index);
        return card;
    }
    public void clear() {
        _cards.clear();
    }
    public boolean pushCard(Card card) {
        if (size() > 52 || _contains(card)) {
            return false;
        }
       _cards.add(card);
       return true;
    }
    public Card getCardAt(int index) {
        return _cards.get(index);
    }
    public int size() {
        return _cards.size();
    }
    public boolean isEmpty() {
        return _cards.size() == 0;
    }
}
