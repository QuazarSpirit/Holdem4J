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
    public enum VariantEnum {
        ROYAL_HOLDEM(2, 20), TEXAS_HOLDEM(2), OMAHA_HOLDEM(4), DRAW(5);

        private final int _pocketCardSize;
        private int _deckSize = 52;

        VariantEnum(int pocketCardSize) {
            _pocketCardSize = pocketCardSize;
        }

        VariantEnum(int pocketCardSize, int deckSize) {
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

    public enum RankVariantEnum {
        HIGH, HIGH_LOW, LOW
    }

    public enum BetStructureEnum {
        FIXED_LIMIT, POT_LIMIT, NO_LIMIT
    }

    public enum MaxSeatsEnum {
        HEADS_UP(2), SHORT_HANDED(6), FULL_RING(10);

        private final int _seats;

        MaxSeatsEnum(int seats) {
            _seats = seats;
        }

        public int toInt() {
            return _seats;
        }
    }

    public enum PlayerTypeEnum {
        MIXED, REAL, AI
    }

    public enum FormatEnum {
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

    private PlayerTypeEnum _player_type = PlayerTypeEnum.MIXED;
    private BetStructureEnum _bet_structure = BetStructureEnum.NO_LIMIT;
    private VariantEnum _variant = VariantEnum.TEXAS_HOLDEM;
    private RankVariantEnum _subVariant = RankVariantEnum.HIGH;
    private FormatEnum _format = FormatEnum.CASHGAME;
    private MaxSeatsEnum _maxSeatsName = MaxSeatsEnum.FULL_RING;
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

    public Game(VariantEnum variant, BetStructureEnum bet_structure) {
        _bet_structure = bet_structure;
        _variant = variant;

        setStackSize(_maxStackSize);
    }

    public Game(VariantEnum variant, BetStructureEnum bet_structure, int maxStackSize) {
        this(variant, bet_structure);
        setStackSize(maxStackSize);
    }

    public Game(VariantEnum variant, BetStructureEnum bet_structure, PlayerTypeEnum playerType) {
        this(variant, bet_structure);
        _player_type = playerType;
    }

    public Game(VariantEnum variant, BetStructureEnum bet_structure, int maxStackSize, MaxSeatsEnum maxSeats) {
        this(variant, bet_structure, maxStackSize);
        _maxSeatsName = maxSeats;
    }

    public Game(VariantEnum variant, BetStructureEnum bet_structure, int maxStackSize, MaxSeatsEnum maxSeats,
            PlayerTypeEnum playerType) {
        this(variant, bet_structure, maxStackSize, maxSeats);
        _player_type = playerType;
    }

    public Game(GameConfigLoader configLoader) {
        this();

        // Only set arguments when not null
        Optional.ofNullable(configLoader.getEnumValue("variant", Game.VariantEnum.class))
                .ifPresent(value -> this._variant = value);

        Optional.ofNullable(configLoader.getEnumValue("betStructure", Game.BetStructureEnum.class))
                .ifPresent(value -> this._bet_structure = value);
        Optional.ofNullable(configLoader.getEnumValue("maxSeats", Game.MaxSeatsEnum.class))
                .ifPresent(value -> this._maxSeatsName = value);

    }

    private void setStackSize(int maxStackSize) {
        _maxStackSize = maxStackSize;
        _minStackSize = _maxStackSize / 5;
    }

    public BetStructureEnum getBetStructure() {
        return _bet_structure;
    }

    public VariantEnum getGameVariant() {
        return _variant;
    }

    public FormatEnum getFormat() {
        return _format;
    }

    public PlayerTypeEnum getPlayerType() {
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
