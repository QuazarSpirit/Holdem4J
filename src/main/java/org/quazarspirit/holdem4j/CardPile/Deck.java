package org.quazarspirit.holdem4j.CardPile;

import java.util.Random;

import org.quazarspirit.holdem4j.Card;

public class Deck extends CardPile {
    protected final DiscardPile discardPile;

    public Deck(int deckMaxSize) {
        super(deckMaxSize);
        this.discardPile = new DiscardPile(deckMaxSize);
    }

    public Deck(String cardRankRange, String cardColorRange) {
        this(cardRankRange.length() * cardColorRange.length());

        try {
            init(cardRankRange, cardColorRange);
        } catch (CardPileOverflowException e) {
            // This case is impossible
            System.exit(-2);
        }
    }

    public Deck(Builder builder) {
        this(builder._cardRankRange, builder._cardColorRange);
    }

    /**
     * Create Deck with a subset Card.RANKS<br>
     * eg: For Royal Poker = "TJQKA"<br>
     * NOTE: This methods does <b>NOT</b> shuffle cards !
     * 
     * @param cardRange subString of valid ranks (eg: A2345)
     * @throws CardPileOverflowException
     */
    public void init(String cardRankRange, String cardColorRange) throws CardPileOverflowException {
        cards.clear();

        char[] colors = cardColorRange.toCharArray();
        char[] ranks = cardRankRange.toCharArray();

        if (colors.length * ranks.length > this._maxSize) {
            throw new CardPileOverflowException();
        }

        for (char color : colors) {
            for (char rank : ranks) {
                Card c = new Card.Builder().color(Character.toString(color)).rank(Character.toString(rank)).build();
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
        Deck shuffledDeck = new Deck(this._maxSize);
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

    public static class Builder {
        private String _cardRankRange;
        private String _cardColorRange;

        public Builder CardRankRange(String cardRankRange) {
            _cardRankRange = cardRankRange;
            return this;
        }

        public Builder CardColorRange(String cardColorRange) {
            _cardColorRange = cardColorRange;
            return this;
        }
    }
}
