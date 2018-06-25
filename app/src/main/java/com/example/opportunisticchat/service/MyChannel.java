package com.example.opportunisticchat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;

import com.example.opportunisticchat.general.Constants;

import java.util.ArrayList;
import java.util.List;

import ro.pub.acs.hyccups.opportunistic.Channel;
import ro.pub.acs.hyccups.opportunistic.Connection;

/**
 * Created by Mihai on 6/19/2018.
 */

public class MyChannel extends Channel {

    private MyBroadcastReceiver mReceiver;

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.CHANNEL_REQUEST)) {
                String request = intent.getStringExtra("request");
                switch (request) {
                    case Constants.REGISTER_CHANNEL:
                        Log.v(Constants.CHANNEL_TAG, "onReceive:" + " " + "register channel request" );
                        String channel = intent.getStringExtra("channel");
                        if (!useCustomSocialInfo() || mRegistry.shouldRegister()) {
                            try {
                                mEngine.register(mChannel);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Constants.UNREGISTER_CHANNEL:
                        Log.v(Constants.CHANNEL_TAG, "onReceive:" + " " + "unregister channel request" );
                        try {
                            mEngine.unregister(mChannel);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.SEND_MESSAGE:
                        Log.v(Constants.CHANNEL_TAG, "onReceive:" + " " + "send message request" );
                        String peer = intent.getStringExtra("peer");
                        String message = intent.getStringExtra("message");
                        Connection connection = new Connection(getApplicationContext(), getName());
                        connection.forward(peer, message);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constants.CHANNEL_REQUEST);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public String getName() {
        Log.v(Constants.CHANNEL_TAG, "getName()");
        return Constants.CHANNEL_NAME;
    }

    @Override
    public List getInterests() { return new ArrayList<String>(); }

    @Override
    public void onRegistered(String channel) {
        Log.v(Constants.CHANNEL_TAG, "onRegistered: " + channel);
        Intent intent = new Intent(Constants.CHANNEL_RESPONSE);
        intent.putExtra(Constants.RESPONSE_TYPE, Constants.CHANNEL_REGISTERED);
        sendBroadcast(intent);
    }

    @Override
    public void onUnregistered(String channel) {
        Log.v(Constants.CHANNEL_TAG, "onUnregistered: " + channel);
        Intent intent = new Intent(Constants.CHANNEL_RESPONSE);
        intent.putExtra(Constants.RESPONSE_TYPE, Constants.CHANNEL_UNREGISTERED);
        sendBroadcast(intent);
    }

    @Override
    public void onPeerConnected(String deviceId, String userId) {
        Log.v(Constants.CHANNEL_TAG, "onPeerConnected: " + userId);
        Intent intent = new Intent(Constants.CHANNEL_RESPONSE);
        intent.putExtra(Constants.RESPONSE_TYPE, Constants.PEER_CONNECTED);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("socialId", userId);
        sendBroadcast(intent);
    }

    @Override
    public void onPeerDisconnected(String deviceId, String userId) {
        Log.v(Constants.CHANNEL_TAG, "onPeerDisconnected: " + userId);
        Intent intent = new Intent(Constants.CHANNEL_RESPONSE);
        intent.putExtra(Constants.RESPONSE_TYPE, Constants.PEER_DISCONNECTED);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("socialId", userId);
        sendBroadcast(intent);
    }

    @Override
    public void onMessageReceived(String sourceUserId, String destinationUserId, String message, long timestamp) {
        Log.v(Constants.CHANNEL_TAG, "onMessageReceived: " + sourceUserId + " " + destinationUserId + " " + " " + message);
        Intent intent = new Intent(Constants.CHANNEL_RESPONSE);
        intent.putExtra(Constants.RESPONSE_TYPE, Constants.PEER_MESSAGE);
        intent.putExtra("socialId", sourceUserId);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    @Override
    public void onDisseminationReceived(String sourceUserId, String message, long timestamp, String[] tags) { }
}
