package org.quazarspirit.holdem4j.player_logic;

import org.quazarspirit.utils.publisher_subscriber_pattern.IEventType;

public enum PLAYER_INTENT implements IEventType {
    JOIN, LEAVE, ACT;
}
