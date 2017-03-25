package com.google.firebase.udacity.friendlychat.domain.repository;

public interface MessageValidator {

    String MAX_MESSAGE_LENGTH_KEY = "key_max_message_length";
    int DEFAULT_MAX_MESSAGE_LENGTH = 1000;

    int getMaxLength();

}
