package com.example.opportunisticchat.adapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.example.opportunisticchat.R;
import com.example.opportunisticchat.general.Constants;
import com.example.opportunisticchat.model.Channel;
import com.example.opportunisticchat.proxy.ServiceProxyOperations;
import com.example.opportunisticchat.view.ChatActivity;
import com.example.opportunisticchat.view.ChatConversationFragment;

public class NetworkServiceAdapter extends BaseAdapter {

    private Context context = null;
    private LayoutInflater layoutInflater = null;

    private List<Channel> data = null;
    private ServiceProxyOperations serviceProxy = null;

    private static class NetworkServiceViewHolder {
        private TextView networkServiceNameTextView;
        private Button networkServiceConnectButton;
    }

    private class NetworkServiceConnectButtonClickListener implements Button.OnClickListener {

        private int channelPosition = -1;
        private int channelType = -1;

        public NetworkServiceConnectButtonClickListener(int channelPosition, int channelType) {
            this.channelPosition = channelPosition;
            this.channelType = channelType;
        }

        @Override
        public void onClick(View view) {
            final Channel chat;
            switch(channelType) {
                case Constants.CONVERSATION_TO_CHANNEL:
                    chat = serviceProxy.getCommunicationToPeerChannels().get(channelPosition);
                    break;
                case Constants.CONVERSATION_FROM_CHANNEL:
                    chat = serviceProxy.getCommunicationFromPeerChannels().get(channelPosition);
                    break;
                default:
                    chat = null;
                    break;
            }

            ChatConversationFragment chatConversationFragment = new ChatConversationFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.CLIENT_POSITION, channelPosition);
            arguments.putInt(Constants.CLIENT_TYPE, channelType);
            chatConversationFragment.setArguments(arguments);
            FragmentManager fragmentManager = ((ChatActivity) context).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_frame_layout, chatConversationFragment, Constants.FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    public NetworkServiceAdapter(Context context, List<Channel> data, ServiceProxyOperations serviceProxy) {
        this.context = context;
        this.data = data;
        this.serviceProxy = serviceProxy;

        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Channel> data) {
        this.data = data;
    }

    public List<Channel> getData() {
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        NetworkServiceViewHolder networkServiceViewHolder;

        Channel channel = (Channel)getItem(position);

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.network_service, parent, false);
            networkServiceViewHolder = new NetworkServiceViewHolder();
            networkServiceViewHolder.networkServiceNameTextView = (TextView)view.findViewById(R.id.networkservice_name_text_view);
            networkServiceViewHolder.networkServiceConnectButton = (Button)view.findViewById(R.id.network_service_connect_button);
            view.setTag(networkServiceViewHolder);
        } else {
            view = convertView;
        }

        networkServiceViewHolder = (NetworkServiceViewHolder)view.getTag();
        networkServiceViewHolder.networkServiceNameTextView.setText(channel.getWellKnownName());
        switch (channel.getChannelType()) {
            case Constants.CONVERSATION_TO_CHANNEL:
                networkServiceViewHolder.networkServiceConnectButton.setText(
                        context.getResources().getString(R.string.connect));
                break;
            case Constants.CONVERSATION_FROM_CHANNEL:
                networkServiceViewHolder.networkServiceConnectButton.setText(
                        context.getResources().getString(R.string.view));
                break;
        }
        networkServiceViewHolder.networkServiceConnectButton.setOnClickListener(
                new NetworkServiceConnectButtonClickListener(
                        position,
                        channel.getChannelType()
                )
        );

        return view;
    }

}
