package org.quazarspirit.holdem4j;

public class Card {
    private final String _color;
    private final String _rank;

    private Card(Builder cardBuilder) {
        this._color = cardBuilder.color;
        this._rank = cardBuilder.rank;
    }

    public String getRank() {
        return this._rank;
    }

    public String getColor() {
        return this._color;
    }

    public String getValue() {
        return new StringBuilder(_rank).append(_color).toString();
    }

    public static class Builder {
        private String color;
        private String rank;

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder rank(String rank) {
            this.rank = rank;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }
}
