package org.quazarspirit.holdem4j;

public class Card {
    private final String _color;
    private final String _rank;

    private Card(CardBuilder cardBuilder) {
        this._color = cardBuilder.color;
        this._rank = cardBuilder.rank;
    }

    public String getRank() {
        return this._rank;
    }

    public int getRankAsInt() {
        return RANKS.indexOf(this._rank);
    }

    public String getColor() {
        return this._color;
    }

    public int getColorAsInt() {
        return COLORS.indexOf(this._color);
    }

    public String getValue() {
        return new StringBuilder(_rank).append(_color).toString();
    }

    public static class CardBuilder {
        private String color;
        private String rank;

        public CardBuilder color(String color) {
            this.color = color;
            return this;
        }

        public CardBuilder rank(String rank) {
            this.rank = rank;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }
}
