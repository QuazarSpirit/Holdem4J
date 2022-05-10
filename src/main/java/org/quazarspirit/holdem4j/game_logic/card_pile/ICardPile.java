package org.quazarspirit.holdem4j.game_logic.card_pile;

import org.quazarspirit.holdem4j.game_logic.Card;

public interface ICardPile {
    public void init();
    public int size();

    public int getMaxSize();
    public boolean isEmpty();
    public boolean pushCard(Card card);
    public Card getCardAt(int index);
    public boolean contains(Card cardToCheck);
    public void sort(CardPile.SORT_CRITERIA criteria);
}
