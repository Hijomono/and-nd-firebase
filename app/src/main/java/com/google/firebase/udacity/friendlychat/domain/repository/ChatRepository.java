package com.google.firebase.udacity.friendlychat.domain.repository;

import android.net.Uri;

import com.google.firebase.udacity.friendlychat.domain.model.FriendlyMessage;

import java.util.List;

public interface ChatRepository {

    void addChatListener(ChatListener listener);

    void removeChatListener(ChatListener listener);

    void addMessage(String message);

    void addPhoto(Uri photoUri);

    interface ChatListener {

        void onChatCleared();

        void onChatLoaded(List<FriendlyMessage> messages);

        void onMessageAdded(FriendlyMessage message);

    }

}
