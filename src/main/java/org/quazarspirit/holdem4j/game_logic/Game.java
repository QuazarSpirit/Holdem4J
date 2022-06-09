package org.quazarspirit.holdem4j.game_logic;

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
        MAX_SEATS(int seats) {_seats = seats;}
        public int toInt() {return _seats;}
    }
    public enum PLAYER_TYPE {
        MIXED, REAL, AI
    }

    private PLAYER_TYPE _player_type = PLAYER_TYPE.MIXED;
    private BET_STRUCTURE _bet_structure = BET_STRUCTURE.NO_LIMIT;
    private VARIANT _variant = VARIANT.TEXAS_HOLDEM;

    private RANK_VARIANT _subVariant = RANK_VARIANT.HIGH;
    private MAX_SEATS _maxSeatsName = MAX_SEATS.FULL_RING;
    private int _maxStackSize = 100;
    private int _minStackSize;
    public Game() {
        setStackSize(_maxStackSize);
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
    public Game(VARIANT variant, BET_STRUCTURE bet_structure, int maxStackSize, MAX_SEATS maxSeats) {
        this(variant, bet_structure, maxStackSize);
        _maxSeatsName = maxSeats;
    }
    public Game(VARIANT variant, BET_STRUCTURE bet_structure, int maxStackSize, MAX_SEATS maxSeats, PLAYER_TYPE playerType) {
        this(variant, bet_structure, maxStackSize, maxSeats);
        _player_type = playerType;
    }
    public Game(VARIANT variant, BET_STRUCTURE bet_structure, PLAYER_TYPE playerType) {
        this(variant, bet_structure);
        _player_type = playerType;
    }
    private void setStackSize(int maxStackSize) {
        _maxStackSize = maxStackSize;
        _minStackSize = _maxStackSize / 5;
    }
    public BET_STRUCTURE getBetStructure() { return _bet_structure; }
    public VARIANT getGameVariant() { return _variant; }
    public PLAYER_TYPE getPlayerType() { return _player_type; }
    public int getBB() { return _maxStackSize / 100; }
    public int getMaxPlayerCount() { return _maxSeatsName.toInt(); }
    public String asString() {
        return   _variant.toString() + " " +
                _bet_structure.toString() + " " +
                _maxSeatsName.toString() + " " +
                _maxStackSize;
    }
}
