package com.example.opportunisticchat.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.opportunisticchat.R;
import com.example.opportunisticchat.general.Constants;
import com.example.opportunisticchat.model.Channel;
import com.example.opportunisticchat.proxy.ChannelProxyOperations;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private ChannelProxyOperations channelProxy = null;

    private boolean channelRegistrationStatus = false;

    private List<Channel> discoveredChannels = null;
    private List<Channel> conversations = null;

    private Handler handler = null;

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate() callback method was invoked!");
        setContentView(R.layout.activity_chat);

        discoveredChannels = new ArrayList<>();
        conversations = new ArrayList<>();

        setChannelProxy(new ChannelProxyOperations(this, Constants.CHANNEL_NAME));
        setChatNetworkServiceFragment(new MainFragment());

        setHandler(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() callback method was invoked!");
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause() callback method was invoked!");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy() callback method was invoked!");
        if (channelProxy != null) {
            if (channelRegistrationStatus) {
                channelProxy.unregisterNetworkChannel();
            }
        }
        super.onDestroy();
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setChannelProxy(ChannelProxyOperations channelProxy) {
        this.channelProxy = channelProxy;
    }

    public ChannelProxyOperations getChannelProxy() {
        return channelProxy;
    }

    public void setChatNetworkServiceFragment(MainFragment mainFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_frame_layout, mainFragment, Constants.FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    public MainFragment getChatNetworkServiceFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.FRAGMENT_TAG);
        if (fragment instanceof MainFragment) {
            return (MainFragment)fragment;
        }
        return null;
    }

    public void setChannelRegistrationStatus(boolean channelRegistrationStatus) {
        this.channelRegistrationStatus = channelRegistrationStatus;
    }

    public boolean getChannelRegistrationStatus() {
        return channelRegistrationStatus;
    }

    public void setDiscoveredChannels(final List<Channel> discoveredChannels) {
        this.discoveredChannels = discoveredChannels;
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainFragment mainFragment = getChatNetworkServiceFragment();
                if (mainFragment != null && mainFragment.isVisible()) {
                    mainFragment.getDiscoveredChannelsAdapter().setData(discoveredChannels);
                    mainFragment.getDiscoveredChannelsAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    public List<Channel> getDiscoveredChannels() {
        return discoveredChannels;
    }

    public void setConversations(final List<Channel> conversations) {
        this.conversations = conversations;
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainFragment mainFragment = getChatNetworkServiceFragment();
                if (mainFragment != null && mainFragment.isVisible()) {
                    mainFragment.getConversationsAdapter().setData(conversations);
                    mainFragment.getConversationsAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    public List<Channel> getConversations() {
        return conversations;
    }

}