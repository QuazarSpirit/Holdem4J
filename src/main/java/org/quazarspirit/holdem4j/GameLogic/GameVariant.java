package org.quazarspirit.holdem4j.GameLogic;

public class GameVariant {
    private int _pocketCardSize;
    private int _deckSize;
    private String _cardRanks;
    private String _cardColors;

    private GameVariant(Builder builder) {
        this._deckSize = builder._deckSize;
        this._pocketCardSize = builder._pocketCardSize;
        this._cardRanks = builder._cardRanks;
        this._cardColors = builder._cardColors;
    }

    public int getDeckSize() {
        return _deckSize;
    }

    public int getPocketCardSize() {
        return _pocketCardSize;
    }

    public String getCardColors() {
        return _cardColors;
    }

    public String getCardRanks() {
        return _cardRanks;
    }

    public static class Builder {
        private int _pocketCardSize;
        private int _deckSize;
        private String _cardRanks;
        private String _cardColors;

        public Builder color(int pocketCardSize) {
            this._pocketCardSize = pocketCardSize;
            return this;
        }

        public Builder rank(int deckSize) {
            this._deckSize = deckSize;
            return this;
        }

        public Builder cardRanks(String cardRanks) {
            this._cardRanks = cardRanks;
            return this;
        }

        public Builder cardColors(String cardColors) {
            this._cardColors = cardColors;
            return this;
        }

        public GameVariant build() {
            return new GameVariant(this);
        }
    }
}
