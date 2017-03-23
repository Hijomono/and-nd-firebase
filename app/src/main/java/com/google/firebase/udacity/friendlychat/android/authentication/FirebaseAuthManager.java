package com.google.firebase.udacity.friendlychat.android.authentication;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public final class FirebaseAuthManager implements AuthenticationManager {

    private final FirebaseAuth auth;
    private final List<AuthenticationChangedListener> listeners;

    private boolean authenticated;

    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();
        listeners = new ArrayList<>();
        authenticated = auth.getCurrentUser() != null;
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final String user = getUser(firebaseAuth);
                final boolean isAuthenticatedNow = user != null;
                if (authenticated != isAuthenticatedNow) {
                    authenticated = isAuthenticatedNow;
                    synchronized (listeners) {
                        for (AuthenticationChangedListener listener : listeners) {
                            listener.onUserAuthenticated(user);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void addAuthenticationChangedListener(final AuthenticationChangedListener listener) {
        synchronized (listeners) {
            listener.onUserAuthenticated(getUser(auth));
            listeners.add(listener);
        }
    }

    @Override
    public void removeAuthenticationChangedListener(final AuthenticationChangedListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private String getUser(final FirebaseAuth firebaseAuth) {
        return firebaseAuth.getCurrentUser() == null ? null : firebaseAuth.getCurrentUser().getDisplayName();
    }

}
