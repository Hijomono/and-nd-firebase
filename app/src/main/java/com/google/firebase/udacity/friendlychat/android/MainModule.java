package com.google.firebase.udacity.friendlychat.android;

import dagger.Module;

@Module
public class MainModule {
    private final FriendlyChatApplication application;

    public MainModule(final FriendlyChatApplication application) {
        this.application = application;
    }

}
