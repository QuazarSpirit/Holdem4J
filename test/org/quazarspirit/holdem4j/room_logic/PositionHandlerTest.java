package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PositionHandlerTest {
    static final  HashMap<Integer, ArrayList<Position.NAME>> testValues = new HashMap<>() {{
        put(6, new ArrayList<>() {{
            add(Position.NAME.SB);
            add(Position.NAME.BB);
            add(Position.NAME.UTG);
            add(Position.NAME.HIJACK);
            add(Position.NAME.CO);
            add(Position.NAME.BTN);
        }});

        put(4, new ArrayList<>() {{
            add(Position.NAME.SB);
            add(Position.NAME.BB);
            add(Position.NAME.UTG);
            add(Position.NAME.BTN);
        }});

        put(2, new ArrayList<>() {{
            add(Position.NAME.SB);
            add(Position.NAME.BB);
        }});
    }};
    static final int FP_ORDER_LENGTH = Position.NAME.DEFAULT.length;
    @Test
    void update() {
        for(Map.Entry<Integer, ArrayList<Position.NAME>> set: testValues.entrySet()) {
            Position p = new Position();
            p.update(10, set.getKey());
            assertEquals(set.getValue(), p.getUsed());
            assertEquals(10 - set.getKey(), p.getFreeCount());
        }
    }

    @Test
    void pickFreePosition() {
        final int maxTableSize = 10;

        for(int currentPlayerCount = 0; currentPlayerCount < maxTableSize; currentPlayerCount++) {
            System.out.println("++++++++++++++++++++++++++\n" + currentPlayerCount + " " + maxTableSize);
            Position p = new Position();
            p.update(maxTableSize, currentPlayerCount);
            assertEquals(FP_ORDER_LENGTH - currentPlayerCount, p.getFreeCount());

            for(int i = currentPlayerCount; i < FP_ORDER_LENGTH; i++) {
                int fp_count = p.getFreeCount();
                System.out.println("-----------------\n" + "Free positions count: " + fp_count + " current player count: " + i);

                assertEquals(fp_count, FP_ORDER_LENGTH - i);
                assertEquals(fp_count, FP_ORDER_LENGTH - i);
                Position.NAME pName = p.pickFree();

                System.out.println(pName + " ");
                assertEquals(Position.NAME.PRIORITY_ORDER[i], pName);
            }
        }
    }

    @Test
    void releaseFreePosition() {
        Position p = new Position();

        p.update(10, 10);
        assertEquals(0, p.getFreeCount());

        int i = Position.NAME.DEFAULT.length;
        while (p.getFreeCount() != FP_ORDER_LENGTH) {
            int fp_count = p.getFreeCount();
            Position.NAME pName = Position.NAME.DEFAULT[i - 1];
            System.out.print(" " + pName + " " + fp_count);
            assertTrue(p.release(pName));
            assertEquals(10 - fp_count, i);
            i--;
        }
    }
}