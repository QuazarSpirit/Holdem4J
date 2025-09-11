package org.quazarspirit.holdem4j.GameLogic;

import java.util.Optional;

import org.quazarspirit.Utils.GameConfigLoader;
import org.quazarspirit.holdem4j.GameLogic.ChipPile.Chip;
import org.quazarspirit.holdem4j.RoomLogic.Table.Table;

/**
 * Structured object for better handling of rules
 */
public class Game {
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
    private GameVariant _variant;
    private RankVariantEnum _subVariant = RankVariantEnum.HIGH;
    private FormatEnum _format = FormatEnum.CASHGAME;
    private MaxSeatsEnum _maxSeatsName = MaxSeatsEnum.FULL_RING;
    private int _maxStackSize = 100;
    private int _minStackSize;
    private Chip _unit;

    public Game(GameConfigLoader configLoader) {
        this._variant = configLoader.getVariant();

        Optional.ofNullable(configLoader.getEnumValue("betStructure", Game.BetStructureEnum.class))
                .ifPresent(value -> this._bet_structure = value);
        Optional.ofNullable(configLoader.getEnumValue("maxSeats", Game.MaxSeatsEnum.class))
                .ifPresent(value -> this._maxSeatsName = value);

    }

    private void setStackSize(int maxStackSize) {
        _maxStackSize = maxStackSize;
        _minStackSize = _maxStackSize / 5;
    }

    public GameVariant getVariant() {
        return _variant;
    }

    public BetStructureEnum getBetStructure() {
        return _bet_structure;
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
        return "\n" + _variant.toString() + "\n" +
                _bet_structure.toString() + "\n" +
                _maxSeatsName.toString() + "\n" +
                _maxStackSize;
    }

    @Override
    public String toString() {
        return asString();
    }
}
