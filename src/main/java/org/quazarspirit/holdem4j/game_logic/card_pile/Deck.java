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
    protected void init() {
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
    public String asString() {
        StringBuilder deckAsString = new StringBuilder();
        for (Object o: cards.toArray()) {
            Card card = (Card) o;
            deckAsString.append(card.getValue()).append("/");
        }

        deckAsString = new StringBuilder(deckAsString.substring(0, deckAsString.length() - 1));
        return deckAsString.toString();
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
    public void clear() {
        cards.clear();
    }
    public DiscardPile getDiscardPile() { return discardPile; }
    public Card getCardAt(int index) {
        return cards.get(index);
    }
}
