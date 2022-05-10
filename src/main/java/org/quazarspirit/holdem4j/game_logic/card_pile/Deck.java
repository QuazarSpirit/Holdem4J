package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;

import java.util.Random;

public class Deck extends CardPile {
    protected DiscardPile discardPile = new DiscardPile();
    public Deck() {
        super();
        init();
    }

    @Override
    public void init() {
        cards.clear();

        for(char color: Card.COLORS.toCharArray()) {
            for(char rank:Card.RANKS.toCharArray()) {
                Card c = new Card(("" + rank + color));
                cards.add(c);
            }
        }
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

    public Card pick(int index) {
        Card card = getCardAt(index);
        cards.remove(index);
        return card;
    }
    public void burn() {
        Card card = pick(0);
        discardPile.pushCard(card);
    }

    public DiscardPile getDiscardPile() { return discardPile; }
}
