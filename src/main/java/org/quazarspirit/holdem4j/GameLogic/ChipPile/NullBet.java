package org.quazarspirit.holdem4j.GameLogic.ChipPile;

import org.quazarspirit.holdem4j.GameLogic.Game;

public class NullBet implements IBet {
    private static NullBet _singleton;

    public static NullBet getSingleton() {
        if (_singleton == null) {
            _singleton = new NullBet();
        }
        return _singleton;
    }

    private NullBet() {
    }

    /**
     * @return
     */
    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * @param count
     */
    @Override
    public void set(int count) {

    }

    /**
     * @return
     */
    @Override
    public int get() {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public String asString() {
        return "NullBet";
    }
}
