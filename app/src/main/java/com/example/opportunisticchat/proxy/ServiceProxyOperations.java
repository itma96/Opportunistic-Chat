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

public class ServiceProxyOperations extends BroadcastReceiver {

    private static final String SERVICE_REQUEST = "com.example.opportunisticchat.service.request";
    private static final String SERVICE_RESPONSE = "com.example.opportunisticchat.service.response";
    
    private Context context = null;
    private ChatActivity chatActivity = null;

    private String serviceName = null;

    private List<Channel> communicationToPeerChannels = null;
    private List<Channel> communicationFromPeerChannels = null;

    public ServiceProxyOperations(final Context context, String serviceName) {

        this.context = context;
        this.serviceName = serviceName;
        this.chatActivity = (ChatActivity) context;

        this.communicationToPeerChannels = new ArrayList<>();
        this.communicationFromPeerChannels = new ArrayList<>();

        IntentFilter filter = new IntentFilter(SERVICE_RESPONSE);
        context.registerReceiver(this, filter);
    }

    public void registerNetworkChannel(String channelName) throws Exception {
        Log.i(Constants.TAG, "Registering network channel...");
        // TODO send broadcast intent to myChannel service to register channelName
    }

    public void unregisterNetworkChannel(String channelName) {
        Log.i(Constants.TAG, "Unregistering network channel...");
        // TODO send broadcast intent to myChannel to unregister channelName
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
        //TODO make connection object and send message
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (SERVICE_RESPONSE.equals(intent.getAction())) {
            String what = intent.getStringExtra("type");
            Handler handler = chatActivity.getHandler();

            final String peerDeviceId;
            final String peerSocialId;
            final String peerWellKnownName;
            final String peerMessage;

            switch (what) {
                case Constants.CHANNEL_REGISTERED:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, (String) "Channel has been registered!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    chatActivity.setServiceRegistrationStatus(true);
                    break;
                case Constants.CHANNEL_UNREGISTERED:
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

                    chatActivity.setServiceRegistrationStatus(false);
                    break;
                case Constants.CHANNEL_DISCOVERED:
                    peerDeviceId = intent.getStringExtra("deviceId");
                    peerSocialId = intent.getStringExtra("socialId");
                    peerWellKnownName = Constants.CHANNEL_NAME + ".d" + peerDeviceId + ".u" + peerSocialId;
                    Log.i(Constants.TAG, "Channel discovered: " + peerWellKnownName);
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
                case Constants.CHANNEL_REMOVED:
                    peerDeviceId = intent.getStringExtra("deviceId");
                    peerSocialId = intent.getStringExtra("socialId");
                    peerWellKnownName = serviceName + ".d" + peerDeviceId + ".u" + peerSocialId;
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
                case Constants.CHANNEL_MESSAGE:
                    peerDeviceId = intent.getStringExtra("deviceId");
                    peerSocialId = intent.getStringExtra("socialId");
                    peerWellKnownName = serviceName + ".d" + peerDeviceId + ".u" + peerSocialId;
                    peerMessage = intent.getStringExtra("message");
                    for (Channel communicationFromChannel : communicationFromPeerChannels) {
                        if (communicationFromChannel.getWellKnownName().equals(peerWellKnownName)) {
                            Message message = new Message(peerMessage, Constants.MESSAGE_TYPE_RECEIVED);
                            communicationFromChannel.getConversationHistory().add(message);
                            if (communicationFromChannel.getContext() != null) {
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
                    return;
            }
        }
    }
}