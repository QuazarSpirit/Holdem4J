package org.quazarspirit.holdem4j.player_logic.player;

import org.quazarspirit.utils.publisher_subscriber_pattern.Event;

import java.util.UUID;

public class RealPlayer extends Player{
    public RealPlayer(UUID uuid, String username) {
        super(uuid, username);
    }
    @Override
    public void update(Event event) {}
}
