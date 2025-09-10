package org.quazarspirit.holdem4j.CardPile;

import java.util.ArrayList;

import org.quazarspirit.holdem4j.Card;

public interface ICardPile {
    public ArrayList<Card> getCards();

    /**
     * @return Number of card currently in card pile.
     */
    public int getSize();

    /**
     * @return Max possible number of card for card pile (usually 52 cards).
     */
    public int getMaxSize();

    /**
     * @return Whether card pile is empty or not.
     */
    public boolean isEmpty();

    /**
     * Add card to pile if it isn't already inside and under max size.
     * 
     * @param card Card to push in card pile.
     * @return If card got successfully pushed.
     */
    public boolean pushCard(Card card);

    /**
     * Add all cards from the cardpile if it isn't already inside and under max
     * size.
     * 
     * @param iCardPile CardPile to push cards from.
     */
    public void pushCard(ICardPile iCardPile);

    /**
     * Return card at defined index.<br>
     * 
     * @throws IndexOutOfBoundsException
     * @param index Index to look in card pile.
     * @return Defined card or NullCard
     */
    public Card getCardAt(int index);

    /**
     * Returns if <b>VALUE</b> of given card is currently in card pile.
     * 
     * @param cardToCheck Card to check, only using his value.
     * @return If card is in card pile.
     */
    public boolean contains(Card cardToCheck);

    /**
     * Sort card pile by defined criteria.
     * 
     * @param criteria Authorized values: SORT_CRITERIA
     */
    public void sort(CardPile.CardCriteriaEnum criteria);

    /**
     * Check if both card pile are equivalent.<br>
     * NOTE: This method <b>DOES</b> sort both card pile.
     * 
     * @param cardPileToCheck ICardPile to compare.
     */
    public boolean equals(ICardPile cardPileToCheck);

    public String asString(CardPile.CardCriteriaEnum sortCriteria);

    /**
     * Returns String representation of CardPile with default sort criteria (
     * <b>VALUE</b> ) and separator ( <b>/</b> )
     * 
     * @return String representation of CardPile
     */
    public String asString();

    public String asString(CardPile.CardCriteriaEnum sortCriteria, String separator);

    /**
     * Empty cards array list.
     */
    public void clear();
}
