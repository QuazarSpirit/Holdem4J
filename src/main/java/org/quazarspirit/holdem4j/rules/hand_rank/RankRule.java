package org.quazarspirit.holdem4j.rules.hand_rank;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import static org.quazarspirit.holdem4j.rules.hand_rank.conditions.PairCondition.isPair;

public abstract class RankRule {
    protected String _name;
    protected String _desc;
    RankRule(String name, String desc) {
        _name = name;
        _desc = desc;
    }

    void evaluate(Facts fact) {
        Rule pairRule = new RuleBuilder()
            .name(_name)
            .description(_desc)
            .priority(1)
                // CONDITION
            .when(isPair())
                // ACTION
            .then(facts -> System.out.println("It is a pair!"))
            .build();

        Rule doublePairRule = new RuleBuilder()
                .name(_name)
                .description(_desc)
                .priority(2)
                // CONDITION
                .when(isPair())
                // ACTION
                .then(facts -> System.out.println("It is a pair!"))
                .build();
    }
}
