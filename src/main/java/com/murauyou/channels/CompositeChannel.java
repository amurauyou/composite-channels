package com.murauyou.channels;

import com.murauyou.channels.client.Channel;

public interface CompositeChannel extends PublishableChannel, PathAware, Channel {

    CompositeChannel get(String name);

}
