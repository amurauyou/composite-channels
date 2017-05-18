package com.murauyou.channels;

import com.murauyou.channels.client.Channel;

public interface PublishableChannel extends Channel {

    /**
     * Publishes the specified Event to the channel
     */
    void publish(Object eventMsg);

}