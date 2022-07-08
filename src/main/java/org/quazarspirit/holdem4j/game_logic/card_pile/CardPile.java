package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.NullCard;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class CardPile implements ICardPile {
    public static final String CARD_CHAR_SEPARATOR = "/";
    public enum SORT_CRITERIA {
        RANK, COLOR, VALUE
    }
    // TODO: Change by rules
    static protected final int _maxSize = 52;
    final protected ArrayList<Card> cards = new ArrayList<Card>();
    @Override
    public boolean contains(Card cardToCheck) {
        for (Card card: cards) {
            if (card.getValue().equals(cardToCheck.getValue())) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(ICardPile cardPileToCheck) {
        this.sort(SORT_CRITERIA.RANK);
        cardPileToCheck.sort(SORT_CRITERIA.RANK);

        if (this.size() != cardPileToCheck.size()) {
            return false;
        }

        for(int i = 0; i < cards.size(); i+=1) {
            if (! this.getCardAt(i).getValue().equals(cardPileToCheck.getCardAt(i).getValue())) {
                return false;
            }
        }

        return true;
    };

    @Override
    public int size() {
        return cards.size();
    }
    @Override
    public int getMaxSize() { return CardPile._maxSize; }
    @Override
    public boolean isEmpty() {
        return cards.size() == 0;
    }
    @Override
    public boolean pushCard(Card card) {
        // Needs to be moved in rule
        if (size() >= getMaxSize() || contains(card)) {
            return false;
        }
        cards.add(card);
        return true;
    }

    public void pushCard(ICardPile iCardPile) {
        if (iCardPile != NullCardPile.GetSingleton()) {
            CardPile cardPile = (CardPile) iCardPile;

            for(Card card: cardPile.cards) {
                pushCard(card);
            }
        }
    }
    @Override
    public Card getCardAt(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        }
        return NullCard.GetSingleton();
    }

    /**
     * @param criteria Authorized values: SORT_CRITERIA
     */
    public void sort(SORT_CRITERIA criteria) {
        for(int i = 0; i < cards.size(); i++) {
            for(int j = i; j < cards.size(); j++) {
                Card card_a = cards.get(i);
                Card card_b = cards.get(j);
                if (compare(card_a, card_b, criteria) < 0) {
                    cards.set(i, card_b);
                    cards.set(j, card_a);
                }
            }
        }
    }
    public String asString(SORT_CRITERIA sortCriteria) {
        StringBuilder cardPileAsString = new StringBuilder();
        for (Object o: cards.toArray()) {
            Card card = (Card) o;
            String card_Str;
            if (sortCriteria == SORT_CRITERIA.COLOR) {
                card_Str = card.getColor();
            } else if (sortCriteria == SORT_CRITERIA.RANK) {
                card_Str = card.getRank();
            } else {
                card_Str = card.getValue();
            }
            cardPileAsString.append(card_Str).append(CARD_CHAR_SEPARATOR);
        }

        // Remove last "/" ($ means last character in regex DSL)
        return cardPileAsString.toString().replaceAll(CARD_CHAR_SEPARATOR + "$", "");
    }

    /**
     * Returns String representation of CardPile with default sort criteria as Value
     * @return String
     */
    public String asString() {
        return asString(SORT_CRITERIA.VALUE);
    }

    public String asString(SORT_CRITERIA sortCriteria, String separator) {
        return asString(sortCriteria).replace(CardPile.CARD_CHAR_SEPARATOR, separator);
    }

    /**
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @param sortCriteria Authorized values: SORT_CRITERIA
     * @return integer
     */
    private int compare(Card o1, Card o2, SORT_CRITERIA sortCriteria) {
        return switch (sortCriteria) {
            case RANK, VALUE -> o2.getRankAsInt()   - o1.getRankAsInt();
            case COLOR -> o2.getColorAsInt() - o1.getColorAsInt();
        };
    }
    public boolean isSorted(SORT_CRITERIA sortCriteria) {
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
