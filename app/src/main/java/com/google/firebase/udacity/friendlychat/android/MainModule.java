package com.google.firebase.udacity.friendlychat.android;

import android.content.Context;

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

}
