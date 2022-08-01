package org.quazarspirit.holdem4j.game_logic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.quazarspirit.holdem4j.TestLifecycle;

import static org.junit.jupiter.api.Assertions.*;

class CardTest extends TestLifecycle {
    @Test
    void constructWithCorrectValue() {
        final String value = "2c";
        Card card = new Card(value);
        assertTrue(card.isValid());
        assertSame(card.getValidState(), Card.VALID_STATE.OK);
    }


    @Test
    void constructorWithCorrectValues() {
        for(char color:Card.COLORS.toCharArray()) {
            for(char rank:Card.RANKS.toCharArray()) {
                Card card = new Card(("" + rank + color));
                assertTrue(card.isValid());
                assertSame(card.getValidState(), Card.VALID_STATE.OK);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "2e", "Bc", "able was I ere I saw elba", "Be" })
    void constructorWithIncorrectValues(String value) {
        Card card = new Card((value));
        assertAll("card",
                () -> assertEquals(card.getValue(), Card.VALID_STATE.NONE.toString()),
                () -> assertFalse(card.isValid()),
                () -> assertNotSame(card.getValidState(), Card.VALID_STATE.OK)
        );
    }
}