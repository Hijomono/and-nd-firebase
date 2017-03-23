package com.google.firebase.udacity.friendlychat.android;

import android.content.Context;

import com.google.firebase.udacity.friendlychat.android.authentication.AuthenticationManager;
import com.google.firebase.udacity.friendlychat.android.authentication.FirebaseAuthManager;
import com.google.firebase.udacity.friendlychat.data.FirebaseChatStorage;
import com.google.firebase.udacity.friendlychat.domain.repository.ChatRepository;

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

}
