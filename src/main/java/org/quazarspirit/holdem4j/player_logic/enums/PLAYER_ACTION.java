package org.quazarspirit.holdem4j.player_logic.enums;

import org.quazarspirit.utils.publisher_subscriber_pattern.IEventType;

import java.util.ArrayList;

public enum PLAYER_ACTION implements IEventType {
    CHECK(false), FOLD(false), CALL(false), BET(true), RAISE(true);

    private final boolean _isAggressive;

    PLAYER_ACTION(boolean isAggressive) {
        _isAggressive = isAggressive;
    }

    public boolean isAggressive() {
        return _isAggressive;
    }
}
