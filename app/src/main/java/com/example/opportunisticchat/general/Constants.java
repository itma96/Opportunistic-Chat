package com.example.opportunisticchat.general;

public interface Constants {

    boolean DEBUG = true;

    final public static String FRAGMENT_TAG = "ContainerFrameLayout";

    final public static String CHANNEL_TAG = "MySuperAwesomeChannel";
    final public static String CHANNEL_NAME = "MySuperAwesomeChannel";

    static final String CHANNEL_REQUEST = "com.example.opportunisticchat.channel.request";
    static final String CHANNEL_RESPONSE = "com.example.opportunisticchat.channel.response";

    final public static String REQUEST_TYPE = "request";
    final public static String RESPONSE_TYPE = "response";
    final public static String REGISTER_CHANNEL = "register";
    final public static String UNREGISTER_CHANNEL = "unregister";
    final public static String SEND_MESSAGE = "send";
    final public static String CHANNEL_REGISTERED = "ChannelRegistered";
    final public static String CHANNEL_UNREGISTERED = "ChannelUnregistered";
    final public static String PEER = "peer";
    final public static String PEER_CONNECTED = "PeerConnected";
    final public static String PEER_DISCONNECTED = "PeerDisconnected";
    final public static String MESSAGE = "message";
    final public static String PEER_MESSAGE = "ChannelMessage";

    final public static int CONVERSATION_TO_CHANNEL = 1;
    final public static int CONVERSATION_FROM_CHANNEL = 2;

    final public static int MESSAGE_TYPE_SENT = 1;
    final public static int MESSAGE_TYPE_RECEIVED = 2;

    final public static String CLIENT_POSITION = "clientPosition";
    final public static String CLIENT_TYPE = "clientType";
}
