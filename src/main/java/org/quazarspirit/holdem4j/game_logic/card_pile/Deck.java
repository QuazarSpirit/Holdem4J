package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.Game;

import java.util.Random;

/**
 *
 */
public class Deck extends CardPile {
    protected final DiscardPile discardPile = new DiscardPile();
    public Deck() {
        super();
        init();
    }

    public Deck(Game game) {
        super();
        init(game.getGameVariant().getCardRanks());
    }

    /**
     *
     */
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

    /**
     * Overloads init default method
     * Meant to create Deck with a subset Card. RANKS
     * eg: Royal Poker
     * @param cardRange subString of valid ranks (eg: A2345)
     */
    public void init(String cardRange) {
        cards.clear();

        for(char color: Card.COLORS.toCharArray()) {
            for(char rank: cardRange.toCharArray()) {
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
        discard(card);
    }

    public void burn(int discardNumber) {
        for(int i = 0; i < discardNumber; i+=1) {
            burn();
        }
    }

    public void discard(Card card) {
        discardPile.pushCard(card);
    }

    public void discard(ICardPile iCardPile) {
        if (iCardPile != NullCardPile.GetSingleton()) {
            CardPile cardPile = (CardPile) iCardPile;
            for(Card card: cardPile.cards) {
                discardPile.pushCard(card);
            }
        }
    }



    public DiscardPile getDiscardPile() { return discardPile; }
}
