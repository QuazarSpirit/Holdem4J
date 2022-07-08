package org.quazarspirit.holdem4j.room_logic;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.quazarspirit.utils.publisher_subscriber_pattern.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PositionHandlerTest extends Publisher {
    static final  HashMap<Integer, ArrayList<POSITION>> testValues = new HashMap<>() {{
        put(6, new ArrayList<>() {{
            add(POSITION.SB);
            add(POSITION.BB);
            add(POSITION.UTG);
            add(POSITION.HIJACK);
            add(POSITION.CO);
            add(POSITION.BTN);
        }});

        put(4, new ArrayList<>() {{
            add(POSITION.SB);
            add(POSITION.BB);
            add(POSITION.UTG);
            add(POSITION.BTN);
        }});

        put(2, new ArrayList<>() {{
            add(POSITION.SB);
            add(POSITION.BB);
        }});
    }};
    static final int FP_ORDER_LENGTH = POSITION.DEFAULT.length;

    public void sendAllocateEvent(int maxSeatCount, int playerCount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PositionHandler.EVENT.ALLOCATE);
        jsonObject.put("max_seat_count", maxSeatCount);
        jsonObject.put("player_count",playerCount);
        publish(jsonObject);
    }
    @Test
    void update() {
        for(Map.Entry<Integer, ArrayList<POSITION>> set: testValues.entrySet()) {
            PositionHandler p = new PositionHandler();
            this.addSubscriber(p);
            sendAllocateEvent(10, set.getKey());
            assertEquals(set.getValue(), p.getUsed());
            assertEquals(10 - set.getKey(), p.getFreeCount());
            this.removeSubscriber(p);
        }
    }

    @Test
    void pickFreePosition() {
        final int maxTableSize = 10;

        for(int currentPlayerCount = 0; currentPlayerCount < maxTableSize; currentPlayerCount++) {
            System.out.println("++++++++++++++++++++++++++\n" + currentPlayerCount + " " + maxTableSize);
            PositionHandler p = new PositionHandler();
            this.addSubscriber(p);
            sendAllocateEvent(maxTableSize, currentPlayerCount);
            //p.update(maxTableSize, currentPlayerCount);
            assertEquals(FP_ORDER_LENGTH - currentPlayerCount, p.getFreeCount());

            for(int i = currentPlayerCount; i < FP_ORDER_LENGTH; i++) {
                int fp_count = p.getFreeCount();
                System.out.println("-----------------\n" + "Free positions count: " + fp_count + " current player count: " + i);

                assertEquals(fp_count, FP_ORDER_LENGTH - i);
                assertEquals(fp_count, FP_ORDER_LENGTH - i);
                POSITION pName = p.pickFree();

                System.out.println(pName + " ");
                assertEquals(POSITION.PRIORITY_ORDER[i], pName);
            }
            this.removeSubscriber(p);
        }
    }

    @Test
    void releaseFreePosition() {
        PositionHandler p = new PositionHandler();

        int playerCount = 10;
        sendAllocateEvent(10, playerCount);
        //p.update(10, playerCount);
        assertEquals(0, p.getFreeCount());

        int i = POSITION.DEFAULT.length;
        while (p.getFreeCount() != FP_ORDER_LENGTH) {
            int fp_count = p.getFreeCount();
            POSITION pName = POSITION.DEFAULT[i - 1];
            System.out.print(" " + pName + " " + fp_count);
            p.releaseUsed(pName);
            assertEquals(playerCount - fp_count, i);
            i--;
        }
    }
}