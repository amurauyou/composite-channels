package com.murauyou.channels.client;

public interface Channel {

    /**
     * Attempts to register the subscriber to the specified Classifier
     */
    void subscribe(Subscriber subscriber);

    /**
     * Attempts to deregister the subscriber from all Classifiers it may be subscribed to
     */
    void unsubscribe(Subscriber subscriber);

}