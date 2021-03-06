package org.quazarspirit.utils;


public class Singleton {
    static private Singleton _singleton = new Singleton();

    protected Singleton() {}
    static public Singleton GetSingleton() {
        return _singleton;
    }

}
