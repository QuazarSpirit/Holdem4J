package org.quazarspirit.holdem4j.GameLogic.CardPile;

import org.quazarspirit.holdem4j.Card.Card;

public interface ICardPile {
    public void init();

    public int size();

    public int getMaxSize();

    public boolean isEmpty();

    public boolean pushCard(Card card);

    public void pushCard(ICardPile iCardPile);

    public Card getCardAt(int index);

    public boolean contains(Card cardToCheck);

    public void sort(CardPile.SORT_CRITERIA criteria);

    public boolean equals(ICardPile cardPileToCheck);

    public String asString(CardPile.SORT_CRITERIA sortCriteria);

    /**
     * Returns String representation of CardPile with default sort criteria as Value
     * 
     * @return String
     */
    public String asString();

    public String asString(CardPile.SORT_CRITERIA sortCriteria, String separator);
}
