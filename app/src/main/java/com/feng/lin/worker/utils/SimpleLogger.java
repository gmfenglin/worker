package com.feng.lin.worker.utils;

public class SimpleLogger {
    private static final SimpleLogger ourInstance = new SimpleLogger();

    public static SimpleLogger getInstance() {
        return ourInstance;
    }

    private SimpleLogger() {
    }
    public void log(String info){
        System.err.println(info);
    }
}
