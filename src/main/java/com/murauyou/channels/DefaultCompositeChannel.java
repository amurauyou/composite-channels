package com.murauyou.channels;

import com.murauyou.channels.client.Subscriber;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCompositeChannel implements CompositeChannel {

    private final String name;
    private final String cachedPath;

    protected final Set<Subscriber> subscriptions = new HashSet<>();

    protected final ConcurrentHashMap<String, CompositeChannel> children = new ConcurrentHashMap<>();

    protected final SubscriptionsBroadcastStrategy broadcastStrategy = new SequentialBroadcastStrategy();


    public DefaultCompositeChannel(String name) {
        this.name = name;
        cachedPath = normalizePath(name);
    }

    protected DefaultCompositeChannel(String name, PathAware parent) {
        this.name = name;
        cachedPath = parent.path() + PATH_DELIMITER + normalizePath(name);
    }

    private String normalizePath(String source) {
        return source.toLowerCase().replaceAll(NORMALIZATION_REGEX, NORMALIZATION_REPLACEMENT);
    }

    @Override
    public String path() {
        return cachedPath;
    }

    @Override
    public CompositeChannel get(String name) {
        return children.computeIfAbsent(name, (k) -> new DefaultCompositeChannel(k, this));
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscriptions.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriptions.remove(subscriber);
    }

    @Override
    public void publish(Object eventMsg) {
        // Publishing event on the current channel
        broadcastStrategy.broadcast(subscriptions, eventMsg);
    }
}