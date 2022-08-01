package org.quazarspirit.holdem4j;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.quazarspirit.utils.Utils;

import java.io.FileReader;
import java.util.ArrayList;

public class RuleEngine {
    public static void main(String[] args) throws Exception {
        //create a person instance (fact)

        Facts facts = new Facts();
        MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
        String fileName = "src/main/java/org/quazarspirit/holdem4j/rule.yml";
        String fileName2 = "src/main/java/org/quazarspirit/holdem4j/rule2.yml";
        Rules alcoholRules = ruleFactory.createRules(new FileReader(fileName));
        Rules nameRules = ruleFactory.createRules(new FileReader(fileName2));

        // create a rule set
        alcoholRules.register();
        nameRules.register();

        //create a default rules engine and fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();

        Utils.Log("Tom: Hi! can I have some Vodka please?");
        rulesEngine.fire(alcoholRules, facts);
        rulesEngine.fire(nameRules, facts);
    }
}
