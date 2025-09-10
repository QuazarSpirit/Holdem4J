package org.quazarspirit.holdem4j.CardPile;

import java.util.Random;

import org.quazarspirit.holdem4j.Card;
import org.quazarspirit.holdem4j.GameLogic.Game;

public class Deck extends CardPile {
    protected final DiscardPile discardPile = new DiscardPile();

    /**
     * Create deck of 52 cards and initialize it.
     */
    public Deck() {
        super();
        init();
    }

    /**
     * Create deck with defined game structure.
     */
    public Deck(Game game) {
        super(game.getGameVariant().getDeckSize());
        init(game.getGameVariant().getCardRanks());
    }

    /**
     * Return a deck with default card in regular poker game mode.<br>
     * This means all cards from 2 to Ace and all colors
     */
    @Override
    public void init() {
        init(Card.RANKS);
    }

    /**
     * Create Deck with a subset Card.RANKS<br>
     * eg: For Royal Poker = "TJQKA"<br>
     * NOTE: This methods does <b>NOT</b> shuffle cards !
     * 
     * @param cardRange subString of valid ranks (eg: A2345)
     */
    public void init(String cardRange) {
        cards.clear();

        for (char color : Card.COLORS.toCharArray()) {
            for (char rank : cardRange.toCharArray()) {
                Card c = new Card(("" + rank + color));
                cards.add(c);
            }
        }
    }

    /**
     * Take this deck instance and randomize it.
     * 
     * @return <b>New Instance</b> of deck shuffled.
     */
    public Deck shuffle() {
        Deck initialDeck = this;
        Deck shuffledDeck = new Deck();
        shuffledDeck.clear();

        Random r = new Random();
        while (!initialDeck.isEmpty()) {
            int index = 0;
            if (initialDeck.getSize() > 1) {
                index = r.nextInt(initialDeck.getSize() - 1);
            }
            Card pickedCard = initialDeck.pick(index);
            shuffledDeck.pushCard(pickedCard);
        }

        return shuffledDeck;
    }

    /**
     * Returns cards at defined index and remove it from deck
     * 
     * @param index Number (under 52 for regular poker) of card
     * @return Card from deck at defined position
     */
    public Card pick(int index) {
        Card card = getCardAt(index);
        cards.remove(index);
        return card;
    }

    /**
     * Take first card of deck and put it in discard pile.
     */
    public void burn() {
        Card card = pick(0);
        discard(card);
    }

    /**
     * Take number of card defined and put them in discard pile.
     * 
     * @param discardNumber Number of card to discard
     */
    public void burn(int discardNumber) {
        for (int i = 0; i < discardNumber; i += 1) {
            burn();
        }
    }

    /**
     * Push card given in discard pile
     * 
     * @param card Card to push in discard pile
     */
    private void discard(Card card) {
        // TODO: Discard only cards that aren't already discarded
        discardPile.pushCard(card);
    }

    /**
     * Take all cards given in card pile and discard them.
     * 
     * @param iCardPile Card pile to discard.
     */
    public void discard(ICardPile iCardPile) {
        if (iCardPile == NullCardPile.GetSingleton()) {
            return;
        }

        CardPile cardPile = (CardPile) iCardPile;
        for (Card card : cardPile.cards) {
            discard(card);
        }
    }

    /**
     * Return discard pile
     */
    public DiscardPile getDiscardPile() {
        return discardPile;
    }
}
