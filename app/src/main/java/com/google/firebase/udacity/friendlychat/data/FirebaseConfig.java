package com.google.firebase.udacity.friendlychat.data;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.udacity.friendlychat.BuildConfig;
import com.google.firebase.udacity.friendlychat.domain.repository.MessageValidator;

import java.util.HashMap;
import java.util.Map;

public final class FirebaseConfig implements MessageValidator {

    private static final int EXPIRATION_TIME = 3600;

    private final FirebaseRemoteConfig config;

    private boolean configFetched;

    public FirebaseConfig() {
        config = FirebaseRemoteConfig.getInstance();
        final FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setConfigSettings(settings);
        config.setDefaults(getDefaultConfigMap());
        fetchIfNeeded();
    }

    @Override
    public int getMaxLength() {
        fetchIfNeeded();
        return ((Long) config.getLong(MessageValidator.MAX_MESSAGE_LENGTH_KEY)).intValue();
    }

    private Map<String, Object> getDefaultConfigMap() {
        final Map<String, Object> configMap = new HashMap<>();
        configMap.put(MessageValidator.MAX_MESSAGE_LENGTH_KEY, MessageValidator.DEFAULT_MAX_MESSAGE_LENGTH);
        return configMap;
    }

    private void fetchIfNeeded() {
        if (!configFetched) {
            config.fetch(BuildConfig.DEBUG ? 0 : EXPIRATION_TIME).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(final Void aVoid) {
                    configFetched = true;
                    config.activateFetched();
                }

            });
        }
    }

}
