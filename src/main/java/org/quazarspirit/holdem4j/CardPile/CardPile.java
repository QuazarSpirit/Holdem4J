package org.quazarspirit.holdem4j.CardPile;

import java.util.ArrayList;
import java.util.Iterator;

import org.quazarspirit.holdem4j.Card;

public abstract class CardPile implements ICardPile {
    public static final String CARD_CHAR_SEPARATOR = "/";

    public enum CardCriteriaEnum {
        RANK, COLOR, VALUE
    }

    private static int _maxSize = 52;
    protected final ArrayList<Card> cards = new ArrayList<Card>();

    CardPile(int deckMaxSize) {
        _maxSize = deckMaxSize;
    }

    @Override
    public boolean contains(Card cardToCheck) {
        for (Card card : cards) {
            if (card.getValue().equals(cardToCheck.getValue())) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(ICardPile cardPileToCheck) {
        if (this.getSize() != cardPileToCheck.getSize()) {
            return false;
        }

        this.sort(CardCriteriaEnum.RANK);
        cardPileToCheck.sort(CardCriteriaEnum.RANK);

        for (int i = 0; i < cards.size(); i += 1) {
            if (!this.getCardAt(i).getValue().equals(cardPileToCheck.getCardAt(i).getValue())) {
                return false;
            }
        }

        return true;
    };

    @Override
    public int getSize() {
        return cards.size();
    }

    @Override
    public int getMaxSize() {
        return CardPile._maxSize;
    }

    @Override
    public boolean isEmpty() {
        return cards.size() == 0;
    }

    @Override
    public boolean pushCard(Card card) {
        if (getSize() >= getMaxSize() || contains(card)) {
            return false;
        }

        cards.add(card);
        return true;
    }

    public void pushCard(ICardPile iCardPile) {
        for (Card card : iCardPile.getCards()) {
            pushCard(card);
        }
    }

    public Card getCardAt(int index) {
        return cards.get(index);
    }

    public void sort(CardCriteriaEnum criteria) {
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i; j < cards.size(); j++) {
                Card card_a = cards.get(i);
                Card card_b = cards.get(j);
                if (compare(card_a, card_b, criteria) < 0) {
                    cards.set(i, card_b);
                    cards.set(j, card_a);
                }
            }
        }
    }

    /**
     * Return String representation of card pile.<br>
     * This <b>DOES NOT</b> sort card pile.
     * 
     * @param sortCriteria Criteria to build representation from, there is no
     *                     sorting there.
     * @return String representation of card pile.
     */
    public String asString(CardCriteriaEnum sortCriteria) {
        StringBuilder cardPileAsString = new StringBuilder();
        for (Object o : cards.toArray()) {
            Card card = (Card) o;
            String card_Str;
            if (sortCriteria == CardCriteriaEnum.COLOR) {
                card_Str = card.getColor();
            } else if (sortCriteria == CardCriteriaEnum.RANK) {
                card_Str = card.getRank();
            } else {
                card_Str = card.getValue();
            }
            cardPileAsString.append(card_Str).append(CARD_CHAR_SEPARATOR);
        }

        // Remove last "/"
        return cardPileAsString.toString().replaceAll(CARD_CHAR_SEPARATOR + "$", "");
    }

    /**
     * Returns String representation of CardPile with defined sortCriteria and
     * separator
     * 
     * @return String representation of CardPile
     */
    public String asString(CardCriteriaEnum sortCriteria, String separator) {
        return asString(sortCriteria).replace(CardPile.CARD_CHAR_SEPARATOR, separator);
    }

    public String asString() {
        return asString(CardCriteriaEnum.VALUE);
    }

    /**
     * @param o1           the first object to be compared.
     * @param o2           the second object to be compared.
     * @param sortCriteria Authorized values: SORT_CRITERIA
     * @return integer
     */
    private int compare(Card o1, Card o2, CardCriteriaEnum sortCriteria) {
        return switch (sortCriteria) {
            case RANK, VALUE -> o2.getRankAsInt() - o1.getRankAsInt();
            case COLOR -> o2.getColorAsInt() - o1.getColorAsInt();
        };
    }

    /**
     * Return if card pile is sorted.
     * Return always true if empty or 1 card.
     * 
     * @param sortCriteria Criteria of sorting
     * @return If card pile is sorted
     */
    public boolean isSorted(CardCriteriaEnum sortCriteria) {
        if (cards.isEmpty() || cards.size() == 1) {
            return true;
        }

        Iterator<Card> iterator = cards.iterator();
        Card current, previous = iterator.next();
        while (iterator.hasNext()) {
            current = iterator.next();
            if (compare(previous, current, sortCriteria) < 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public void clear() {
        cards.clear();
    }

    public abstract void init();
}
