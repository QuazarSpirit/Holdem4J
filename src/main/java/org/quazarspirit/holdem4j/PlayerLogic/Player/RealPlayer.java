package org.quazarspirit.holdem4j.PlayerLogic.Player;

import java.util.UUID;

import org.quazarspirit.Utils.PubSub.Event;

public class RealPlayer extends Player {
    public RealPlayer(UUID uuid, String username) {
        super(uuid, username);
    }

    @Override
    public void update(Event event) {
        System.out.println("Got event: " + " " + event);
    }
}
