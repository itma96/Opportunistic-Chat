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
import com.example.opportunisticchat.adapter.NetworkServiceAdapter;
import com.example.opportunisticchat.general.Constants;
import com.example.opportunisticchat.proxy.ServiceProxyOperations;

public class ChatNetworkServiceFragment extends Fragment {

    private Button registerServiceButton = null;
    private Button unregisterServiceButton = null;

    private ChatActivity chatActivity = null;
    private ServiceProxyOperations serviceProxyOperations = null;

    private View view = null;

    private NetworkServiceAdapter discoveredServicesAdapter = null;
    private NetworkServiceAdapter conversationsAdapter = null;

    private ListView discoveredServicesListView = null;
    private ListView conversationsListView = null;


    private registerServiceButtonListener registerServiceButtonListener = new registerServiceButtonListener();
    private class registerServiceButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!chatActivity.getServiceRegistrationStatus()) {
                try {
                    serviceProxyOperations.registerNetworkChannel(Constants.SERVICE_NAME);
                } catch (Exception exception) {
                    Log.e(Constants.TAG, "Could not register network service: " + exception.getMessage());
                    if (Constants.DEBUG) {
                        exception.printStackTrace();
                    }
                    return;
                }
                //chatActivity.setServiceRegistrationStatus(!chatActivity.getServiceRegistrationStatus());
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
            if (chatActivity.getServiceRegistrationStatus()) {
                serviceProxyOperations.unregisterNetworkChannel(Constants.SERVICE_NAME);
                //chatActivity.setServiceRegistrationStatus(!chatActivity.getServiceRegistrationStatus());
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
        Log.i(Constants.TAG, "ChatNetworkServiceFragment -> onCreateView() callback method was invoked");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat_network_service, parent, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        Log.i(Constants.TAG, "ChatNetworkServiceFragment -> onActivityCreated() callback method was invoked");


        registerServiceButton = (Button)getActivity().findViewById(R.id.start_service_button);
        registerServiceButton.setOnClickListener(registerServiceButtonListener);
        unregisterServiceButton = (Button)getActivity().findViewById(R.id.stop_service_button);
        unregisterServiceButton.setOnClickListener(unregisterServiceButtonListener);


        chatActivity = (ChatActivity)getActivity();
        serviceProxyOperations = chatActivity.getServiceProxyOperations();

        discoveredServicesListView = (ListView)getActivity().findViewById(R.id.discovered_services_list_view);
        discoveredServicesAdapter = new NetworkServiceAdapter(chatActivity, chatActivity.getDiscoveredChannels(), chatActivity.getServiceProxyOperations());
        discoveredServicesListView.setAdapter(discoveredServicesAdapter);

        conversationsListView = (ListView)getActivity().findViewById(R.id.conversations_list_view);
        conversationsAdapter = new NetworkServiceAdapter(chatActivity, chatActivity.getConversations(), chatActivity.getServiceProxyOperations());
        conversationsListView.setAdapter(conversationsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setDiscoveredServicesAdapter(NetworkServiceAdapter discoveredServicesAdapter) {
        this.discoveredServicesAdapter = discoveredServicesAdapter;
    }

    public NetworkServiceAdapter getDiscoveredServicesAdapter() {
        return discoveredServicesAdapter;
    }

    public void setConversationsAdapter(NetworkServiceAdapter conversationsAdapter) {
        this.conversationsAdapter = conversationsAdapter;
    }

    public NetworkServiceAdapter getConversationsAdapter() {
        return conversationsAdapter;
    }

}
