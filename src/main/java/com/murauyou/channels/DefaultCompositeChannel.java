package com.murauyou.channels;

import com.murauyou.channels.client.Subscriber;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default composite channel consists of few concerns:
 *  - all activities done to the parent channel (current composite) propagate to its children:
 *    - subscribers subscribe to this channel and children channels
 *    - subscribers unsubscribe from this channel and children channels
 *    - event is being published as narrow as possible thought - only to this channel and NOT children
 *  - event publishing to a collection of subscribers is random, sequencing and synchronous
 *
 * This class should NOT be exposed to clients for use as it provides methods for managing channel tree (adding children)
 * which malicious client could use to create infinite loop of tree building.
 */
public class DefaultCompositeChannel implements CompositeChannel {

    private final String name;
    private final String cachedPath;

    protected final Set<Subscriber> subscriptions = new HashSet<>();

    protected final ConcurrentHashMap<String, CompositeChannel> children = new ConcurrentHashMap<>();

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
        return children.computeIfAbsent(name, (k) -> {
            CompositeChannel channel = new DefaultCompositeChannel(k, this);
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

        // Unsubscribing from all children channels by default
        children.values().forEach(channel -> channel.unsubscribe(subscriber));
    }

    private void subscribeAllCurrentSubscribersToChildChannel(CompositeChannel childChannel) {
        subscriptions.forEach(childChannel::subscribe);
    }

    @Override
    public void publish(Object eventMsg) {
        // Publishing event on the current channel
        subscriptions.forEach(subscriber -> subscriber.onTrigger(eventMsg));

        // NOT publishing event on children channels
        // children.values().stream().forEach(channel -> channel.publish(eventMsg));
    }
}