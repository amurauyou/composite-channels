package com.murauyou.channels;

import com.murauyou.channels.client.Subscriber;

import java.util.Set;

/**
 * Created by amurauyou on 5/18/17.
 */
public interface SubscriptionsBroadcastStrategy {

    void broadcast(Set<Subscriber> subscriptions, Object eventMsg);

}
