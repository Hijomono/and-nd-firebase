package com.google.firebase.udacity.friendlychat.android;

import com.google.firebase.udacity.friendlychat.android.authentication.AuthenticationManager;
import com.google.firebase.udacity.friendlychat.domain.repository.ChatRepository;
import com.google.firebase.udacity.friendlychat.domain.repository.MessageValidator;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MainModule.class)
public interface MainComponent {

    AuthenticationManager provideAuthenticationManager();

    ChatRepository provideChatRepository();

    MessageValidator provideMessageValidator();

}
