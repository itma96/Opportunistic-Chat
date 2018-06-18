package com.example.opportunisticchat.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    private String wellKnownName;
    private String socialId;
    private String deviceId;
    private int channelType;
    private List<Message> conversationHistory = new ArrayList<>();

    private Context context = null;

    public Channel() {
        this.wellKnownName = new String();
        this.socialId = new String();
        this.deviceId = new String();
        this.channelType = -1;
        this.context = null;
    }

    public Channel(Context context, String wellKnownName, String socialId, String deviceId, int channelType) {
        this.context = context;
        this.wellKnownName = wellKnownName;
        this.socialId = socialId;
        this.deviceId = deviceId;
        this.channelType = channelType;
    }

    public void setWellKnownName(String wellKnownName) {
        this.wellKnownName = wellKnownName;
    }

    public String getWellKnownName() {
        return wellKnownName;
    }

    public void setServiceHostSocialId(String socialId) { this.socialId = socialId; }

    public String getServiceHostSocialId() {
        return socialId;
    }

    public void setServiceHostDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getServiceHostDeviceId() {
        return deviceId;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public int getChannelType() {
        return channelType;
    }

    public List<Message> getConversationHistory() { return conversationHistory; }

    public void setConversationHistory(List<Message> conversationHistory) { this.conversationHistory = conversationHistory; }

    public Context getContext() { return context; }

    public void setContext(Context context) { this.context = context; }

    @Override
    public boolean equals(Object networkService) {
        if (networkService == null) {
            return false;
        }
        if (wellKnownName == null) {
            return false;
        }
        return wellKnownName.equals(((Channel)networkService).getWellKnownName());
    }

}
