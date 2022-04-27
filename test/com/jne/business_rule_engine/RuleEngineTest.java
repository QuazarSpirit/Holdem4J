package com.jne.business_rule_engine;

import com.jne.holdem4j.game_logic.Deck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleEngineTest {

     @Test
     void constructor() {
         RuleEngine re = new RuleEngine("com.jne.holdem4j.rules.game");
         Deck d = new Deck();
         re.validate(d);
     }

    @Test
    void validate() {
    }
}