package org.quazarspirit.holdem4j.PlayerLogic;

import org.quazarspirit.Utils.PubSub.IEventType;

public enum PlayerIntentEnum implements IEventType {
    JOIN, LEAVE, ACT;
}
