package com.jne.business_rule_engine;

import java.lang.reflect.Method;

public abstract class Checkable implements ICheckable {
    @Override
    public Object getAttribute(String name) {
        Class c = this.getClass();

        try {
            Method method =  c.getDeclaredMethod(name);
            return method.invoke(this);
        } catch (Exception e) {
            return null;
        }
    }
}
