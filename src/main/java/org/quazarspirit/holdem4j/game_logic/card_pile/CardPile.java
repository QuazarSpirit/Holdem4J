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

    private static int _maxSize = 52;
    protected final ArrayList<Card> cards = new ArrayList<Card>();

    CardPile() {
    }

    CardPile(int deckMaxSize) {
        _maxSize = deckMaxSize;
    }

    /**
     * Returns if <b>VALUE</b> of given card is currently in card pile.
     * 
     * @param cardToCheck Card to check, only using his value.
     * @return If card is in card pile.
     */
    @Override
    public boolean contains(Card cardToCheck) {
        for (Card card : cards) {
            if (card.getValue().equals(cardToCheck.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if both card pile are equivalent.<br>
     * NOTE: This method <b>DOES</b> sort both card pile.
     * 
     * @param cardPileToCheck ICardPile to compare.
     */
    public boolean equals(ICardPile cardPileToCheck) {
        if (this.size() != cardPileToCheck.size()) {
            return false;
        }

        this.sort(SORT_CRITERIA.RANK);
        cardPileToCheck.sort(SORT_CRITERIA.RANK);

        for (int i = 0; i < cards.size(); i += 1) {
            if (!this.getCardAt(i).getValue().equals(cardPileToCheck.getCardAt(i).getValue())) {
                return false;
            }
        }

        return true;
    };

    /**
     * @return Number of card currently in card pile.
     */
    @Override
    public int size() {
        return cards.size();
    }

    /**
     * @return Max possible number of card for card pile (usually 52 cards).
     */
    @Override
    public int getMaxSize() {
        return CardPile._maxSize;
    }

    /**
     * @return If card pile is empty or not.
     */
    @Override
    public boolean isEmpty() {
        return cards.size() == 0;
    }

    /**
     * Push card if it isn't already inside and under max size.
     * 
     * @param card Card to push in card pile.
     * @return If card got successfully pushed.
     */
    @Override
    public boolean pushCard(Card card) {
        if (size() >= getMaxSize() || contains(card)) {
            return false;
        }

        cards.add(card);
        return true;
    }

    /**
     * @param iCardPile CardPile to push cards from.
     */
    public void pushCard(ICardPile iCardPile) {
        if (iCardPile == NullCardPile.GetSingleton()) {
            return;
        }

        CardPile cardPile = (CardPile) iCardPile;
        for (Card card : cardPile.cards) {
            pushCard(card);
        }
    }

    /**
     * Return card at defined index.<br>
     * If index isn't in correct range return NullCard
     * 
     * @param index Index to look in card pile.
     * @return Defined card or NullCard
     */
    @Override
    public Card getCardAt(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        }
        return NullCard.GetSingleton();
    }

    /**
     * Sort card pile by defined criteria.
     * 
     * @param criteria Authorized values: SORT_CRITERIA
     */
    public void sort(SORT_CRITERIA criteria) {
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
    public String asString(SORT_CRITERIA sortCriteria) {
        StringBuilder cardPileAsString = new StringBuilder();
        for (Object o : cards.toArray()) {
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

        // Remove last "/"
        return cardPileAsString.toString().replaceAll(CARD_CHAR_SEPARATOR + "$", "");
    }

    /**
     * Returns String representation of CardPile with defined sortCriteria and
     * separator
     * 
     * @return String representation of CardPile
     */
    public String asString(SORT_CRITERIA sortCriteria, String separator) {
        return asString(sortCriteria).replace(CardPile.CARD_CHAR_SEPARATOR, separator);
    }

    /**
     * Returns String representation of CardPile with default sort criteria (
     * <b>VALUE</b> ) and separator ( <b>/</b> )
     * 
     * @return String representation of CardPile
     */
    public String asString() {
        return asString(SORT_CRITERIA.VALUE);
    }

    /**
     * @param o1           the first object to be compared.
     * @param o2           the second object to be compared.
     * @param sortCriteria Authorized values: SORT_CRITERIA
     * @return integer
     */
    private int compare(Card o1, Card o2, SORT_CRITERIA sortCriteria) {
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

    /**
     * Empty cards array list.
     */
    public void clear() {
        cards.clear();
    }

    public abstract void init();
}
