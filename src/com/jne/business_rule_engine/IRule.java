package com.jne.business_rule_engine;
import java.util.List;


public interface IRule {
    public boolean shouldRun(ICheckable checkable);
    public List<String> runRule(ICheckable checkable);
}