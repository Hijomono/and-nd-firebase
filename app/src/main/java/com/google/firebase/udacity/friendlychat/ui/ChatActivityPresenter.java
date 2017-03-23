package com.google.firebase.udacity.friendlychat.ui;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.udacity.friendlychat.android.ActivityScope;
import com.google.firebase.udacity.friendlychat.android.authentication.AuthenticationManager;
import com.google.firebase.udacity.friendlychat.domain.model.FriendlyMessage;
import com.google.firebase.udacity.friendlychat.domain.repository.ChatRepository;

import java.util.List;

import javax.inject.Inject;

@ActivityScope
public final class ChatActivityPresenter implements
        AuthenticationManager.AuthenticationChangedListener, ChatRepository.ChatListener {

    private final ChatRepository chatRepository;
    private final AuthenticationManager authenticationManager;

    private View view;

    @Inject
    public ChatActivityPresenter(
            final ChatRepository chatRepository,
            final AuthenticationManager authenticationManager) {

        this.chatRepository = chatRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void onUserAuthenticated(@Nullable final String userName) {
        if (hasView()) {
            if (userName == null) {
                view.showNotAuthenticated();
            } else {
                view.showAuthenticated(userName);
            }
        }
    }

    @Override
    public void onChatCleared() {
        if (hasView()) {
            view.clearChat();
        }
    }

    @Override
    public void onChatLoaded(final List<FriendlyMessage> messages) {
        if (hasView()) {
            view.showMessages(messages);
        }
    }

    @Override
    public void onMessageAdded(final FriendlyMessage message) {
        if (hasView()) {
            view.addMessage(message);
        }
    }

    public void attach(@NonNull final View view) {
        if (this.view != null) {
            throw new IllegalStateException("Tried to attach an already attached presenter");
        }
        this.view = view;
        authenticationManager.addAuthenticationChangedListener(this);
        chatRepository.addChatListener(this);
    }

    public void detach(@NonNull final View view) {
        if (this.view != view) {
            throw new IllegalStateException(hasView() ? "Tried to detach from a wrong view" : "Tried to detach a non attached presenter");
        }
        chatRepository.removeChatListener(this);
        authenticationManager.removeAuthenticationChangedListener(this);
        this.view = null;
    }

    public void sendMessage(final String message) {
        chatRepository.addMessage(message);
    }

    public void sendPhoto(final Uri photoUri) {
        chatRepository.addPhoto(photoUri);
    }

    private boolean hasView() {
        return view != null;
    }

    public interface View {

        void clearChat();

        void addMessage(FriendlyMessage message);

        void showMessages(List<FriendlyMessage> messages);

        void showAuthenticated(String userName);

        void showNotAuthenticated();

    }

}
