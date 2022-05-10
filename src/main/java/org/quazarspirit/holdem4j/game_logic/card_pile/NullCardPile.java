package org.quazarspirit.holdem4j.game_logic.card_pile;


import org.quazarspirit.holdem4j.game_logic.Card;

public class NullCardPile implements ICardPile {
    static final private NullCardPile _singleton = new NullCardPile();
    private NullCardPile() {}

    static public NullCardPile GetSingleton() {
        return _singleton;
    }

    @Override
    public boolean contains(Card cardToCheck) {
        return false;
    }

    /**
     * @param criteria
     */
    @Override
    public void sort(CardPile.SORT_CRITERIA criteria) {}

    @Override
    public int size() {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public int getMaxSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void init() {}

    @Override
    public boolean pushCard(Card card) {
        return false;
    }

    @Override
    public Card getCardAt(int index) {
        return null;
    }
}
