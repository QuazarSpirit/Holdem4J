package com.jne.business_rule_engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleRegistry {
    private ArrayList<IRule> _rules = new ArrayList<IRule>();
    final private String _packageName;

    private boolean _isValid = false;

    RuleRegistry(String packageName) {
        _packageName = packageName;
        _isValid = refreshRules();
    }

    private boolean fillFromPkg() {
        Set<Class> ruleClasses = getAllClassesFromPkg(_packageName);
        if (ruleClasses.size() == 0) return false;

        for (Object o:ruleClasses.toArray()) {
            System.out.println(o.getClass().getName());
            Class c = (Class) o;

            try {
                IRule newRule = (IRule) c.getDeclaredConstructor().newInstance();
                _rules.add(newRule);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public boolean refreshRules() {
        _rules.clear();
        return fillFromJson() && fillFromPkg();
    }

    public boolean isValid() {
        return _isValid;
    }

    private boolean fillFromJson() { return true; }

    private Set<Class> getAllClassesFromPkg(String packageName) {
        String path = packageName.replaceAll("[.]", "/");
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
            System.out.println(e);
        }
        return null;
    }

    public ArrayList<IRule> getRules() {
        return _rules;
    }
}