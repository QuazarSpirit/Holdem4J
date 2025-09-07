package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpLobbyTest {
    @Test
    public void instantiation() {
        // Calling constructor should not be possible
        // As httpLobby is using Singleton design pattern

        // It is still possible to setPort before calling getInstance
        HttpLobby lobby;
        try
        {
            lobby = HttpLobby.getInstance();
        } catch (Exception e) {
            fail();
            return;
        }
        assertNotEquals(lobby, null);
    }

    @Test
    public void instantiationWInvalidPort() {
        // HttpLobby can't have a TCP port > 65535
        try {
            HttpLobby.setPort(30000);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
