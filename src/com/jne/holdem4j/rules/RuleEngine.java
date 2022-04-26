package com.jne.holdem4j.rules;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.FileReader;

public class RuleEngine {

    public static void main(String[] args) throws Exception {
        //create a person instance (fact)
        Person tom = new Person("Tom", 14);

        Facts facts = new Facts();
        facts.put("person", tom);

        // create rules
        MVELRule ageRule = new MVELRule()
                .name("age rule")
                .description("Check if person's age is > 18 and mark the person as adult")
                .priority(1)
                .when("person.age > 18")
                .then("person.setAdult(true);");

        MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
        String fileName = args.length != 0 ? args[0] : "easy-rules-tutorials/src/main/java/org/jeasy/rules/tutorials/shop/alcohol-rule.yml";
        Rule alcoholRule = ruleFactory.createRule(new FileReader(fileName));

        // create a rule set
        Rules rules = new Rules();
        rules.register(ageRule);
        rules.register(alcoholRule);

        //create a default rules engine and fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();

        System.out.println("Tom: Hi! can I have some Vodka please?");
        rulesEngine.fire(rules, facts);
    }

}
