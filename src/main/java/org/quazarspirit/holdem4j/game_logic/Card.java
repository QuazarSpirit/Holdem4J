package org.quazarspirit.holdem4j.game_logic;

import java.util.*;

@SuppressWarnings("ALL")
public class Card {
    static enum VALID_STATE {
        NONE, OK, INVALID_RANK, INVALID_COLOR, INVALID_LENGTH
    };
    public static final String RANKS = "23456789TJQKA";
    public static final String COLORS = "cdhs";
    private String _value =  VALID_STATE.NONE.toString();
    private String _rank = VALID_STATE.INVALID_RANK.toString();
    private String _color = VALID_STATE.INVALID_COLOR.toString();
    private boolean _valid = false;
    private VALID_STATE _valid_state = VALID_STATE.NONE;
    public Card(String value) {
        super();

        // Needs to be moved in CardRule
        if (value.length() != 2) {
            if (! value.equals("")) {
                this._valid_state = VALID_STATE.INVALID_LENGTH;
            }
            return ;
        }

        String rank = String.valueOf(value.charAt(0));
        if (RANKS.indexOf(rank) == -1) {
            // "Rank is invalid (valid range 1..13)"
            this._valid_state = VALID_STATE.INVALID_RANK;
            return ;
        }

        String color = String.valueOf(value.charAt(1));
        if (COLORS.indexOf(color) == -1) {
            // "Color is invalid (valid range 1..4)"
            this._valid_state = VALID_STATE.INVALID_COLOR;
            return ;
        }

        this._value = value;
        this._rank  = rank;
        this._color = color;
        this._valid = true;
        this._valid_state = VALID_STATE.OK;
    }// constructor()
    public boolean isValid() {
        return this._valid;
    }
    public VALID_STATE getValidState() { return this._valid_state; }
    public String getRank() {
        return this._rank;
    }
    public String getColor() {
        return this._color;
    }
    public String getValue() { return this._value; }
    public static void main(String[] args) {
        while(true) {
            System.out.println("Type q to exit");
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter card rank (" + RANKS + "):");
            String rank = sc.nextLine();

            if (rank.equals("q")) {
                break;
            }

            System.out.println("Enter card colour (" + COLORS + "):");
            String color = sc.nextLine();

            if (color.equals("q")) {
                break;
            }


            String card_value = rank + color;
            System.out.println(card_value);

            try {
                Card card = new Card(card_value);
                System.out.println("Poker card value: " + card.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }// main()
}
