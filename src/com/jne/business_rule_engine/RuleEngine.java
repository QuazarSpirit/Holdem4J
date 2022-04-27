package com.jne.business_rule_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// https://tenmilesquare.com/resources/software-development/basic-rules-engine-design-pattern/
public class RuleEngine {
    private final RuleRegistry _rule_registry;
    public RuleEngine(String rulePkg){
        _rule_registry = new RuleRegistry(rulePkg);
    }
    public List<String> validate(ICheckable checkable){
        if (checkable == null){
            return Arrays.asList("Rule is null");
        }

        List<String> errors = new ArrayList<String>();

        for ( IRule rule : _rule_registry.getRules()){
            System.out.println(rule.shouldRun(checkable));
            System.out.println(rule.runRule(checkable));
            /*
            if(rule.shouldRun(checkable)){
                errors.addAll(rule.runRule(checkable));
            }*/
        }
        return errors;
    }
}
