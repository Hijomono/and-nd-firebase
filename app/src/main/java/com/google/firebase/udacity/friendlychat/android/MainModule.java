package com.google.firebase.udacity.friendlychat.android;

import android.content.Context;

import com.google.firebase.udacity.friendlychat.android.authentication.AuthenticationManager;
import com.google.firebase.udacity.friendlychat.android.authentication.FirebaseAuthManager;
import com.google.firebase.udacity.friendlychat.data.FirebaseChatStorage;
import com.google.firebase.udacity.friendlychat.data.FirebaseConfig;
import com.google.firebase.udacity.friendlychat.domain.repository.ChatRepository;
import com.google.firebase.udacity.friendlychat.domain.repository.MessageValidator;
import com.google.firebase.udacity.friendlychat.ui.UiConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private final FriendlyChatApplication application;

    public MainModule(final FriendlyChatApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    public Context provideApplicationContext() {
        return application;
    }

    @Singleton
    @Provides
    public AuthenticationManager provideAuthenticationManager() {
        return new FirebaseAuthManager();
    }

    @Singleton
    @Provides
    public ChatRepository provideChatRepository(final AuthenticationManager authenticationManager) {
        return new FirebaseChatStorage(authenticationManager);
    }

    @Singleton
    @Provides
    public FirebaseConfig provideFirebaseConfig() {
        return new FirebaseConfig();
    }

    @Singleton
    @Provides
    public MessageValidator provideMessageValidator(final FirebaseConfig firebaseConfig) {
        return firebaseConfig;
    }

    @Singleton
    @Provides
    public UiConfig provideUiConfig(final FirebaseConfig firebaseConfig) {
        return firebaseConfig;
    }

}
