package org.quazarspirit.holdem4j.PlayerLogic;

import java.util.ArrayList;

import org.quazarspirit.Utils.PubSub.IEventType;

public enum PlayerActionEnum implements IEventType {
    CHECK(false), FOLD(false), CALL(false), BET(true), RAISE(true);

    private final boolean _isAggressive;

    PlayerActionEnum(boolean isAggressive) {
        _isAggressive = isAggressive;
    }

    public boolean isAggressive() {
        return _isAggressive;
    }
}
