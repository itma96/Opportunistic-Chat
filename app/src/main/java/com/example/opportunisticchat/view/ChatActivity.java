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
import com.example.opportunisticchat.proxy.ServiceProxyOperations;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private ServiceProxyOperations serviceProxyOperations = null;

    private boolean serviceRegistrationStatus = false;

    private List<Channel> discoveredChannels = null;
    private List<Channel> conversations = null;

    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "onCreate() callback method was invoked!");
        setContentView(R.layout.activity_chat);

        discoveredChannels = new ArrayList<>();
        conversations = new ArrayList<>();

        setServiceProxyOperations(new ServiceProxyOperations(this, Constants.SERVICE_NAME));
        setChatNetworkServiceFragment(new ChatNetworkServiceFragment());

        setHandler(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "onResume() callback method was invoked!");
    }

    @Override
    protected void onPause() {
        Log.i(Constants.TAG, "onPause() callback method was invoked!");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "onDestroy() callback method was invoked!");
        if (serviceProxyOperations != null) {
            if (serviceRegistrationStatus) {
                serviceProxyOperations.unregisterNetworkChannel(Constants.SERVICE_NAME);
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

    public void setServiceProxyOperations(ServiceProxyOperations serviceProxyOperations) {
        this.serviceProxyOperations = serviceProxyOperations;
    }

    public ServiceProxyOperations getServiceProxyOperations() {
        return serviceProxyOperations;
    }

    public void setChatNetworkServiceFragment(ChatNetworkServiceFragment chatNetworkServiceFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_frame_layout, chatNetworkServiceFragment, Constants.FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    public ChatNetworkServiceFragment getChatNetworkServiceFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.FRAGMENT_TAG);
        if (fragment instanceof  ChatNetworkServiceFragment) {
            return (ChatNetworkServiceFragment)fragment;
        }
        return null;
    }

    public void setServiceRegistrationStatus(boolean serviceRegistrationStatus) {
        this.serviceRegistrationStatus = serviceRegistrationStatus;
    }

    public boolean getServiceRegistrationStatus() {
        return serviceRegistrationStatus;
    }

    public void setDiscoveredChannels(final List<Channel> discoveredChannels) {
        this.discoveredChannels = discoveredChannels;
        handler.post(new Runnable() {
            @Override
            public void run() {
                ChatNetworkServiceFragment chatNetworkServiceFragment = getChatNetworkServiceFragment();
                if (chatNetworkServiceFragment != null && chatNetworkServiceFragment.isVisible()) {
                    chatNetworkServiceFragment.getDiscoveredServicesAdapter().setData(discoveredChannels);
                    chatNetworkServiceFragment.getDiscoveredServicesAdapter().notifyDataSetChanged();
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
                ChatNetworkServiceFragment chatNetworkServiceFragment = getChatNetworkServiceFragment();
                if (chatNetworkServiceFragment != null && chatNetworkServiceFragment.isVisible()) {
                    chatNetworkServiceFragment.getConversationsAdapter().setData(conversations);
                    chatNetworkServiceFragment.getConversationsAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    public List<Channel> getConversations() {
        return conversations;
    }

}