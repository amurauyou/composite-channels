package com.murauyou.channels.client;

@FunctionalInterface
public interface Subscriber {
    void onTrigger(Object eventMsg);
}