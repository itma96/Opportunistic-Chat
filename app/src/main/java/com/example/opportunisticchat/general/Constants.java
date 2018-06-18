package com.example.opportunisticchat.general;

public interface Constants {

    boolean DEBUG = true;

    final public static String TAG = "[Chat Channel]";
    final public static String FRAGMENT_TAG = "ContainerFrameLayout";

    final public static String SERVICE_NAME = "MyChat";
    final public static String CHANNEL_NAME = "MySuperAwesomeChannel";

    final public static String CHANNEL_REGISTERED = "ChannelRegistered";
    final public static String CHANNEL_UNREGISTERED = "ChannelUnregistered";
    final public static String CHANNEL_DISCOVERED = "ChannelDiscovered";
    final public static String CHANNEL_REMOVED = "ChannelRemoved";
    final public static String CHANNEL_MESSAGE = "ChannelMessage";

    final public static int CONVERSATION_TO_CHANNEL = 1;
    final public static int CONVERSATION_FROM_CHANNEL = 2;

    final public static int MESSAGE_TYPE_SENT = 1;
    final public static int MESSAGE_TYPE_RECEIVED = 2;

    final public static String CLIENT_POSITION = "clientPosition";
    final public static String CLIENT_TYPE = "clientType";


}
