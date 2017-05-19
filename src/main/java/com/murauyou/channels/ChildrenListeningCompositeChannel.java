package com.murauyou.channels;

import com.murauyou.channels.client.Subscriber;

public class ChildrenListeningCompositeChannel extends DefaultCompositeChannel {

    public ChildrenListeningCompositeChannel(String name) {
        super(name);
    }

    public ChildrenListeningCompositeChannel(String name, PathAware parent) {
        super(name, parent);
    }

    @Override
    public CompositeChannel get(String name) {
        return children.computeIfAbsent(name, (k) -> {
            CompositeChannel channel = new ChildrenListeningCompositeChannel(k, this);
            subscribeAllCurrentSubscribersToChildChannel(channel);
            return channel;
        });
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscriptions.add(subscriber);

        // Subscribing to all children channels by default
        children.values().forEach(channel -> channel.subscribe(subscriber));
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriptions.remove(subscriber);

        // Unsubscribe from all children channels by default
        children.values().forEach(channel -> channel.unsubscribe(subscriber));
    }

    private void subscribeAllCurrentSubscribersToChildChannel(CompositeChannel childChannel) {
        subscriptions.forEach(childChannel::subscribe);
    }
}