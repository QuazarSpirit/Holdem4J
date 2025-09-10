package org.quazarspirit.holdem4j.GameLogic;

import java.util.Optional;

import org.quazarspirit.Utils.GameConfigLoader;
import org.quazarspirit.holdem4j.Card;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.Chip;
import org.quazarspirit.holdem4j.RoomLogic.Table;

/**
 * Structured object for better handling of rules
 */
public class Game {
    public enum VARIANT {
        ROYAL_HOLDEM(2, 20), TEXAS_HOLDEM(2), OMAHA_HOLDEM(4), DRAW(5);

        private final int _pocketCardSize;
        private int _deckSize = 52;

        VARIANT(int pocketCardSize) {
            _pocketCardSize = pocketCardSize;
        }

        VARIANT(int pocketCardSize, int deckSize) {
            this(pocketCardSize);
            _deckSize = deckSize;
        }

        public int getPocketCardSize() {
            return _pocketCardSize;
        }

        public int getDeckSize() {
            return _deckSize;
        }

        public String getCardRanks() {
            if (_deckSize == 20) {
                return "TJQKA";
            }

            return Card.RANKS;
        }
    }

    public enum RANK_VARIANT {
        HIGH, HIGH_LOW, LOW
    }

    public enum BET_STRUCTURE {
        FIXED_LIMIT, POT_LIMIT, NO_LIMIT
    }

    public enum MAX_SEATS {
        HEADS_UP(2), SHORT_HANDED(6), FULL_RING(10);

        private final int _seats;

        MAX_SEATS(int seats) {
            _seats = seats;
        }

        public int toInt() {
            return _seats;
        }
    }

    public enum PLAYER_TYPE {
        MIXED, REAL, AI
    }

    public enum FORMAT {
        CASHGAME, SIT_AND_GO, SPIN_AND_PLAY, TOURNAMENT;

        public boolean canStart(Table table) {
            if (this == CASHGAME) {
                return table.getPlayerCount() >= 2;
            } else if (this == TOURNAMENT) {
                // TODO: Implements prize pool
                return table.getPlayerCount() == table.getGame().getMaxSeatsCount();
            } else {
                return table.getPlayerCount() == table.getGame().getMaxSeatsCount();
            }
        }
    }

    private PLAYER_TYPE _player_type = PLAYER_TYPE.MIXED;
    private BET_STRUCTURE _bet_structure = BET_STRUCTURE.NO_LIMIT;
    private VARIANT _variant = VARIANT.TEXAS_HOLDEM;
    private RANK_VARIANT _subVariant = RANK_VARIANT.HIGH;
    private FORMAT _format = FORMAT.CASHGAME;
    private MAX_SEATS _maxSeatsName = MAX_SEATS.FULL_RING;
    private int _maxStackSize = 100;
    private int _minStackSize;
    private Chip _unit;

    /**
     * Create game instance with following structure:<br>
     * <b>Player type:</b> Mixed<br>
     * <b>Bet structure:</b> No Limit<br>
     * <b>Variant:</b> Texas Holdem<br>
     * <b>Sub variant:</b> HIGH<br>
     * <b>Format:</b> Cash game<br>
     * <b>Max seats:</b> Full ring<br>
     * <b>Max stack size:</b> 100
     */
    public Game() {
        setStackSize(_maxStackSize);
        _unit = new Chip(this);
    }

    public Game(VARIANT variant, BET_STRUCTURE bet_structure) {
        _bet_structure = bet_structure;
        _variant = variant;

        setStackSize(_maxStackSize);
    }

    public Game(VARIANT variant, BET_STRUCTURE bet_structure, int maxStackSize) {
        this(variant, bet_structure);
        setStackSize(maxStackSize);
    }

    public Game(VARIANT variant, BET_STRUCTURE bet_structure, PLAYER_TYPE playerType) {
        this(variant, bet_structure);
        _player_type = playerType;
    }

    public Game(VARIANT variant, BET_STRUCTURE bet_structure, int maxStackSize, MAX_SEATS maxSeats) {
        this(variant, bet_structure, maxStackSize);
        _maxSeatsName = maxSeats;
    }

    public Game(VARIANT variant, BET_STRUCTURE bet_structure, int maxStackSize, MAX_SEATS maxSeats,
            PLAYER_TYPE playerType) {
        this(variant, bet_structure, maxStackSize, maxSeats);
        _player_type = playerType;
    }

    public Game(GameConfigLoader configLoader) {
        this();

        // Only set arguments when not null
        Optional.ofNullable(configLoader.getEnumValue("variant", Game.VARIANT.class))
                .ifPresent(value -> this._variant = value);

        Optional.ofNullable(configLoader.getEnumValue("betStructure", Game.BET_STRUCTURE.class))
                .ifPresent(value -> this._bet_structure = value);
        Optional.ofNullable(configLoader.getEnumValue("maxSeats", Game.MAX_SEATS.class))
                .ifPresent(value -> this._maxSeatsName = value);

    }

    private void setStackSize(int maxStackSize) {
        _maxStackSize = maxStackSize;
        _minStackSize = _maxStackSize / 5;
    }

    public BET_STRUCTURE getBetStructure() {
        return _bet_structure;
    }

    public VARIANT getGameVariant() {
        return _variant;
    }

    public FORMAT getFormat() {
        return _format;
    }

    public PLAYER_TYPE getPlayerType() {
        return _player_type;
    }

    public int getBB() {
        return _maxStackSize / 100;
    }

    public Chip getUnit() {
        return _unit;
    }

    public int getMaxStackSize() {
        return _maxStackSize;
    }

    public int getMaxSeatsCount() {
        return _maxSeatsName.toInt();
    }

    public String asString() {
        return _variant.toString() + " " +
                _bet_structure.toString() + " " +
                _maxSeatsName.toString() + " " +
                _maxStackSize;
    }
}
