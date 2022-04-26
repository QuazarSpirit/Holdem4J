package com.jne.holdem4j.room_logic;

import com.jne.holdem4j.room_logic.PositionHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionHandlerTest {
    static final int FP_ORDER_LENGTH = PositionHandler.POSITION_NAME.FREE_POSITION_ORDER.length;

    @Test
    void pickFreePosition() {
        PositionHandler p = new PositionHandler();
        final int maxTableSize = 10;

        p.update(maxTableSize, 2);
        assertEquals(p.getFreePositionCount(), FP_ORDER_LENGTH);

        int i = 0;
        while (p.getFreePositionCount() > 0) {
            int fp_count = p.getFreePositionCount();

            assertEquals(fp_count, FP_ORDER_LENGTH - i);
            assertEquals(fp_count, FP_ORDER_LENGTH - i);
            PositionHandler.POSITION_NAME pName = p.pickFreePosition();

            System.out.print(pName + " ");
            assertEquals(pName,
                    PositionHandler.POSITION_NAME.FREE_POSITION_ORDER[FP_ORDER_LENGTH - i - 1]);
            assertEquals(pName,
                    PositionHandler.POSITION_NAME.SEAT_POSITION_ORDER[i + 2]);
            i++;
        }
    }

    @Test
    void releaseFreePosition() {
        PositionHandler p = new PositionHandler();

        p.update(10, 10);
        assertEquals(p.getFreePositionCount(), 0);
        System.out.print(p.getFreePositionCount() + " ");

        int i = 0;
        while (p.getFreePositionCount() != FP_ORDER_LENGTH) {
            int fp_count = p.getFreePositionCount();
            PositionHandler.POSITION_NAME pName =
                    PositionHandler.POSITION_NAME.FREE_POSITION_ORDER[FP_ORDER_LENGTH - i - 1];
            assertTrue(p.releasePosition(pName));
            assertEquals(fp_count, i);
            i++;
        }
    }
}