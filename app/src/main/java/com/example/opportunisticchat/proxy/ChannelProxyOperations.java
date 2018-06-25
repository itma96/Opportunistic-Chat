package com.example.opportunisticchat.proxy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.example.opportunisticchat.general.Constants;
import com.example.opportunisticchat.model.Channel;
import com.example.opportunisticchat.model.Message;
import com.example.opportunisticchat.view.ChatActivity;
import com.example.opportunisticchat.view.ChatConversationFragment;

public class ChannelProxyOperations extends BroadcastReceiver {

    private Context context = null;
    private ChatActivity chatActivity = null;

    private String channelName = null;

    private List<Channel> communicationToPeerChannels = null;
    private List<Channel> communicationFromPeerChannels = null;
    
    private static final String TAG = "ChannelProxyOperations";

    public ChannelProxyOperations(final Context context, String channelName) {

        this.context = context;
        this.channelName = channelName;
        this.chatActivity = (ChatActivity) context;

        this.communicationToPeerChannels = new ArrayList<>();
        this.communicationFromPeerChannels = new ArrayList<>();

        IntentFilter filter = new IntentFilter(Constants.CHANNEL_RESPONSE);
        context.registerReceiver(this, filter);
    }

    public void registerNetworkChannel(String channelName) throws Exception {
        Log.i(TAG, "Registering network channel...");
        Intent intent = new Intent(Constants.CHANNEL_REQUEST);
        intent.putExtra(Constants.REQUEST_TYPE, Constants.REGISTER_CHANNEL);
        context.sendBroadcast(intent);
    }

    public void unregisterNetworkChannel() {
        Log.i(TAG, "Unregistering network channel...");
        Intent intent = new Intent(Constants.CHANNEL_REQUEST);
        intent.putExtra(Constants.REQUEST_TYPE, Constants.UNREGISTER_CHANNEL);
        context.sendBroadcast(intent);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Channel> getCommunicationToPeerChannels() {
        return communicationToPeerChannels;
    }

    public void setCommunicationToPeerChannels(List<Channel> communicationToPeerChannels) {
        this.communicationToPeerChannels = communicationToPeerChannels;
    }

    public List<Channel> getCommunicationFromPeerChannels() {
        return communicationFromPeerChannels;
    }

    public void setCommunicationFromPeerChannels(List<Channel> communicationFromPeerChannels) {
        this.communicationFromPeerChannels = communicationFromPeerChannels;
        chatActivity.setConversations(communicationFromPeerChannels);
    }

    public void onSendMessage(String userId, String content) {
        Log.i(TAG, "Sending message " + content + " " + "to peer " + userId);
        Intent intent = new Intent(Constants.CHANNEL_REQUEST);
        intent.putExtra(Constants.REQUEST_TYPE, Constants.SEND_MESSAGE);
        intent.putExtra(Constants.PEER, userId);
        intent.putExtra(Constants.MESSAGE, content);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Constants.CHANNEL_RESPONSE.equals(intent.getAction())) {
            String response = intent.getStringExtra(Constants.RESPONSE_TYPE);
            Handler handler = chatActivity.getHandler();

            final String peerDeviceId;
            final String peerSocialId;
            final String peerWellKnownName;
            final String peerMessage;

            switch (response) {
                case Constants.CHANNEL_REGISTERED:
                    Log.i(TAG, "Channel Registered");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, (String) "Channel has been registered!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    chatActivity.setChannelRegistrationStatus(true);
                    break;
                case Constants.CHANNEL_UNREGISTERED:
                    Log.i(TAG, "Channel Unregistered");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, (String) "Channel has been unregistered!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    communicationToPeerChannels.clear();
                    List<Channel> discoveredChannels = chatActivity.getDiscoveredChannels();
                    discoveredChannels.clear();
                    chatActivity.setDiscoveredChannels(discoveredChannels);

                    communicationFromPeerChannels.clear();
                    List<Channel> conversations = chatActivity.getConversations();
                    conversations.clear();
                    chatActivity.setConversations(conversations);

                    chatActivity.setChannelRegistrationStatus(false);
                    break;
                case Constants.PEER_CONNECTED:
                    peerDeviceId = intent.getStringExtra("deviceId");
                    peerSocialId = intent.getStringExtra("socialId");
                    peerWellKnownName = channelName + ".d" + peerDeviceId + ".u" + peerSocialId;
                    Log.i(TAG, "Peer Connected: " + peerWellKnownName);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            List<Channel> discoveredChannels = chatActivity.getDiscoveredChannels();
                            for (Channel discoveredChannel : discoveredChannels) {
                                if (discoveredChannel.getWellKnownName().equals(peerWellKnownName))
                                    return;
                            }
                            Channel channel = new Channel(null, peerWellKnownName, peerSocialId, peerDeviceId, Constants.CONVERSATION_TO_CHANNEL);
                            communicationToPeerChannels.add(channel);
                            discoveredChannels.add(channel);
                            chatActivity.setDiscoveredChannels(discoveredChannels);
                        }
                    });
                    break;
                case Constants.PEER_DISCONNECTED:
                    peerDeviceId = intent.getStringExtra("deviceId");
                    peerSocialId = intent.getStringExtra("socialId");
                    peerWellKnownName = channelName + ".d" + peerDeviceId + ".u" + peerSocialId;
                    Log.i(TAG, "Peer Disconnected: " + peerWellKnownName);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Channel peerChannel = null;
                            List<Channel> discoveredChannels = chatActivity.getDiscoveredChannels();
                            for (Channel discoveredChannel : discoveredChannels) {
                                if (discoveredChannel.getWellKnownName().equals(peerWellKnownName)) {
                                    peerChannel = discoveredChannel;
                                }
                            }
                            if (peerChannel != null) {
                                discoveredChannels.remove(peerChannel);
                                communicationToPeerChannels.remove(peerChannel);
                                chatActivity.setDiscoveredChannels(discoveredChannels);
                            }
                        }
                    });
                    break;
                case Constants.PEER_MESSAGE:
                    peerDeviceId = intent.getStringExtra("socialId");
                    peerSocialId = intent.getStringExtra("socialId");
                    peerWellKnownName = channelName + ".d" + peerDeviceId + ".u" + peerSocialId;
                    Log.i(TAG, "Peer Message: " + peerWellKnownName);
                    peerMessage = intent.getStringExtra("message");
                    for (Channel communicationFromPeerChannel : communicationFromPeerChannels) {
                        if (communicationFromPeerChannel.getWellKnownName().equals(peerWellKnownName)) {
                            Message message = new Message(peerMessage, Constants.MESSAGE_TYPE_RECEIVED);
                            communicationFromPeerChannel.getConversationHistory().add(message);
                            if (communicationFromPeerChannel.getContext() != null) {
                                ChatActivity chatActivity = (ChatActivity)context;
                                FragmentManager fragmentManager = chatActivity.getFragmentManager();
                                Fragment fragment = fragmentManager.findFragmentByTag(Constants.FRAGMENT_TAG);
                                if (fragment instanceof ChatConversationFragment && fragment.isVisible()) {
                                    ChatConversationFragment chatConversationFragment = (ChatConversationFragment)fragment;
                                    chatConversationFragment.appendMessage(message);
                                }
                            }
                            return;
                        }
                    }
                    Channel peerChannel = new Channel(null, peerWellKnownName, peerSocialId, peerDeviceId, Constants.CONVERSATION_FROM_CHANNEL);
                    Message message = new Message(peerMessage, Constants.MESSAGE_TYPE_RECEIVED);
                    peerChannel.getConversationHistory().add(message);
                    communicationFromPeerChannels.add(peerChannel);
                    setCommunicationFromPeerChannels(communicationFromPeerChannels);
                    break;
                default:
                    break;
            }
        }
    }
}