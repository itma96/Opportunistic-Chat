package com.example.opportunisticchat.view;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.opportunisticchat.R;
import com.example.opportunisticchat.adapter.ChannelAdapter;
import com.example.opportunisticchat.general.Constants;
import com.example.opportunisticchat.proxy.ChannelProxyOperations;

public class MainFragment extends Fragment {

    private Button registerChannelButton = null;
    private Button unregisterChannelButton = null;

    private ChatActivity chatActivity = null;
    private ChannelProxyOperations channelProxy = null;

    private View view = null;

    private ChannelAdapter discoveredChannelsAdapter = null;
    private ChannelAdapter conversationsAdapter = null;

    private ListView discoveredChannelsListView = null;
    private ListView conversationsListView = null;

    private static final String TAG = "MainFragment";

    private registerServiceButtonListener registerServiceButtonListener = new registerServiceButtonListener();
    private class registerServiceButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!chatActivity.getChannelRegistrationStatus()) {
                try {
                    channelProxy.registerNetworkChannel(Constants.CHANNEL_NAME);
                } catch (Exception exception) {
                    Log.e(TAG, "Could not register network channel: " + exception.getMessage());
                    if (Constants.DEBUG) {
                        exception.printStackTrace();
                    }
                    return;
                }
                //chatActivity.setChannelRegistrationStatus(!chatActivity.getChannelRegistrationStatus());
            }
            else {
                chatActivity.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(chatActivity, (String) "Channel already registered!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private unregisterServiceButtonListener unregisterServiceButtonListener = new unregisterServiceButtonListener();
    private class unregisterServiceButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (chatActivity.getChannelRegistrationStatus()) {
                channelProxy.unregisterNetworkChannel();
                //chatActivity.setChannelRegistrationStatus(!chatActivity.getChannelRegistrationStatus());
            }
            else {
                chatActivity.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(chatActivity, (String) "Channel already unregistered!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        Log.i(TAG, "MainFragment -> onCreateView() callback method was invoked");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, parent, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        Log.i(TAG, "MainFragment -> onActivityCreated() callback method was invoked");


        registerChannelButton = (Button)getActivity().findViewById(R.id.register_channel_button);
        registerChannelButton.setOnClickListener(registerServiceButtonListener);
        unregisterChannelButton = (Button)getActivity().findViewById(R.id.unregister_channel_button);
        unregisterChannelButton.setOnClickListener(unregisterServiceButtonListener);


        chatActivity = (ChatActivity)getActivity();
        channelProxy = chatActivity.getChannelProxy();

        discoveredChannelsListView = (ListView)getActivity().findViewById(R.id.discovered_services_list_view);
        discoveredChannelsAdapter = new ChannelAdapter(chatActivity, chatActivity.getDiscoveredChannels(), chatActivity.getChannelProxy());
        discoveredChannelsListView.setAdapter(discoveredChannelsAdapter);

        conversationsListView = (ListView)getActivity().findViewById(R.id.conversations_list_view);
        conversationsAdapter = new ChannelAdapter(chatActivity, chatActivity.getConversations(), chatActivity.getChannelProxy());
        conversationsListView.setAdapter(conversationsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setDiscoveredChannelsAdapter(ChannelAdapter discoveredChannelsAdapter) {
        this.discoveredChannelsAdapter = discoveredChannelsAdapter;
    }

    public ChannelAdapter getDiscoveredChannelsAdapter() {
        return discoveredChannelsAdapter;
    }

    public void setConversationsAdapter(ChannelAdapter conversationsAdapter) {
        this.conversationsAdapter = conversationsAdapter;
    }

    public ChannelAdapter getConversationsAdapter() {
        return conversationsAdapter;
    }

}
