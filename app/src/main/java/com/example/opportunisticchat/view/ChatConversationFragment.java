package com.example.opportunisticchat.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.opportunisticchat.R;
import com.example.opportunisticchat.general.Constants;
import com.example.opportunisticchat.model.Message;
import com.example.opportunisticchat.model.Channel;
import com.example.opportunisticchat.proxy.ChannelProxyOperations;

public class ChatConversationFragment extends Fragment {

    private LinearLayout chatCommunicationHistoryLinearLayout = null;
    private EditText messageEditText = null;
    private Button sendMessageButton = null;

    private Channel peerChannel = null;

    private int peerPosition;
    private int peerType;

    private static final String TAG = "ChatConversationFragment";

    private SendMessageButtonClickListener sendMessageButtonClickListener = new SendMessageButtonClickListener();
    private class SendMessageButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String message = messageEditText.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(getActivity(), "You should fill a message!", Toast.LENGTH_SHORT).show();
            } else {
                ChatActivity chatActivity = (ChatActivity)getActivity();
                ChannelProxyOperations channelProxyOperations = chatActivity.getChannelProxy();
                channelProxyOperations.onSendMessage(peerChannel.getServiceHostSocialId(), message);
                peerChannel.getConversationHistory().add(new Message(message, Constants.MESSAGE_TYPE_SENT));

                FragmentManager fragmentManager = chatActivity.getFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(Constants.FRAGMENT_TAG);
                if (fragment instanceof ChatConversationFragment && fragment.isVisible()) {
                    ChatConversationFragment chatConversationFragment = (ChatConversationFragment) fragment;
                    chatConversationFragment.appendMessage(new Message(message, Constants.MESSAGE_TYPE_SENT));
                }

                messageEditText.setText("");
            }
        }
    }

    public ChatConversationFragment() {
        this.peerPosition = -1;
        this.peerType = -1;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle state) {
        return layoutInflater.inflate(R.layout.fragment_chat_conversation, parent, false);
    }

    public synchronized void appendMessage(final Message message) {
        chatCommunicationHistoryLinearLayout.post(new Runnable() {
            @Override
            public void run() {
                TextView messageTextView = new TextView(getActivity());
                messageTextView.setText(message.getContent());
                LinearLayout.LayoutParams messageTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                switch(message.getType()) {
                    case Constants.MESSAGE_TYPE_SENT:
                        messageTextView.setBackgroundResource(R.drawable.frame_border_sent_message);
                        messageTextView.setGravity(Gravity.LEFT);
                        messageTextViewLayoutParams.gravity = Gravity.LEFT;
                        break;
                    case Constants.MESSAGE_TYPE_RECEIVED:
                        messageTextView.setBackgroundResource(R.drawable.frame_border_received_message);
                        messageTextView.setGravity(Gravity.RIGHT);
                        messageTextViewLayoutParams.gravity = Gravity.RIGHT;
                        break;
                }

                chatCommunicationHistoryLinearLayout.addView(messageTextView, messageTextViewLayoutParams);

                Space space = new Space(getActivity());
                LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                chatCommunicationHistoryLinearLayout.addView(space, spaceLayoutParams);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        this.peerPosition = arguments.getInt(Constants.CLIENT_POSITION, -1);
        this.peerType = arguments.getInt(Constants.CLIENT_TYPE, -1);

        ChatActivity chatServiceActivity = (ChatActivity)getActivity();
        ChannelProxyOperations channelProxyOperations = chatServiceActivity.getChannelProxy();

        switch (peerType) {
            case Constants.CONVERSATION_TO_CHANNEL:
                peerChannel = channelProxyOperations.getCommunicationToPeerChannels().get(peerPosition);
                break;
            case Constants.CONVERSATION_FROM_CHANNEL:
                peerChannel = channelProxyOperations.getCommunicationFromPeerChannels().get(peerPosition);
                break;
        }

        chatCommunicationHistoryLinearLayout = (LinearLayout)getActivity().findViewById(R.id.chat_communication_history_linear_layout);
        chatCommunicationHistoryLinearLayout.removeAllViews();
        messageEditText = (EditText)getActivity().findViewById(R.id.message_edit_text);

        sendMessageButton = (Button)getActivity().findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(sendMessageButtonClickListener);

        if (peerChannel != null) {
            peerChannel.setContext(chatServiceActivity);
            List<Message> conversationHistory = peerChannel.getConversationHistory();
            for (Message conversation: conversationHistory) {
                appendMessage(conversation);
            }
        }
    }

    public int getPeerPosition() {
        return peerPosition;
    }

    public int getPeerType() {
        return peerType;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
