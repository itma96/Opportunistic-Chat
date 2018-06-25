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
import com.example.opportunisticchat.proxy.ChannelProxyOperations;
import com.example.opportunisticchat.view.ChatActivity;
import com.example.opportunisticchat.view.ChatConversationFragment;

public class ChannelAdapter extends BaseAdapter {

    private Context context = null;
    private LayoutInflater layoutInflater = null;

    private List<Channel> data = null;
    private ChannelProxyOperations channelProxy = null;

    private static class ChannelViewHolder {
        private TextView networkServiceNameTextView;
        private Button channelConnectButton;
    }

    private class ChannelConnectButtonClickListener implements Button.OnClickListener {

        private int channelPosition = -1;
        private int channelType = -1;

        public ChannelConnectButtonClickListener(int channelPosition, int channelType) {
            this.channelPosition = channelPosition;
            this.channelType = channelType;
        }

        @Override
        public void onClick(View view) {
            final Channel chat;
            switch(channelType) {
                case Constants.CONVERSATION_TO_CHANNEL:
                    chat = channelProxy.getCommunicationToPeerChannels().get(channelPosition);
                    break;
                case Constants.CONVERSATION_FROM_CHANNEL:
                    chat = channelProxy.getCommunicationFromPeerChannels().get(channelPosition);
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

    public ChannelAdapter(Context context, List<Channel> data, ChannelProxyOperations channelProxy) {
        this.context = context;
        this.data = data;
        this.channelProxy = channelProxy;

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

        ChannelViewHolder channelViewHolder;

        Channel channel = (Channel)getItem(position);

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.channel, parent, false);
            channelViewHolder = new ChannelViewHolder();
            channelViewHolder.networkServiceNameTextView = (TextView)view.findViewById(R.id.channel_name_text_view);
            channelViewHolder.channelConnectButton = (Button)view.findViewById(R.id.channel_connect_button);
            view.setTag(channelViewHolder);
        } else {
            view = convertView;
        }

        channelViewHolder = (ChannelViewHolder)view.getTag();
        channelViewHolder.networkServiceNameTextView.setText(channel.getWellKnownName());
        switch (channel.getChannelType()) {
            case Constants.CONVERSATION_TO_CHANNEL:
                channelViewHolder.channelConnectButton.setText(
                        context.getResources().getString(R.string.connect));
                break;
            case Constants.CONVERSATION_FROM_CHANNEL:
                channelViewHolder.channelConnectButton.setText(
                        context.getResources().getString(R.string.view));
                break;
        }
        channelViewHolder.channelConnectButton.setOnClickListener(
                new ChannelConnectButtonClickListener(
                        position,
                        channel.getChannelType()
                )
        );

        return view;
    }

}
