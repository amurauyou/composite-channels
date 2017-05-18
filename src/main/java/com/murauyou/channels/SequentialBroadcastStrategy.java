package com.murauyou.channels;

import com.murauyou.channels.client.Subscriber;

import java.util.Set;

public class SequentialBroadcastStrategy implements SubscriptionsBroadcastStrategy {
    @Override
    public void broadcast(Set<Subscriber> subscriptions, Object eventMsg) {
        subscriptions.forEach(subscriber -> subscriber.onTrigger(eventMsg));
    }
}
