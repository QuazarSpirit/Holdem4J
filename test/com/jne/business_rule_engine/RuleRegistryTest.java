package com.jne.business_rule_engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleRegistryTest {

    @Test
    void constructor() {
        // Package name should be stored in config file
        RuleRegistry ruleReg = new RuleRegistry("com.jne.holdem4j.rules.game");
        assertTrue(ruleReg.isValid());
        System.out.println(ruleReg.getRules());
    }


    @Test
    void getRules() {
    }
}