package com.google.firebase.udacity.friendlychat.android;

import android.app.Application;

public final class FriendlyChatApplication extends Application {

    private MainComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build();
    }

    public MainComponent getComponent() {
        return component;
    }

}
