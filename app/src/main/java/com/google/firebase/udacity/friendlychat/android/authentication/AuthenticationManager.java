package com.google.firebase.udacity.friendlychat.android.authentication;

import android.support.annotation.Nullable;

public interface AuthenticationManager {

    void addAuthenticationChangedListener(AuthenticationChangedListener listener);

    void removeAuthenticationChangedListener(AuthenticationChangedListener listener);

    interface AuthenticationChangedListener {

        /**
         * Called on each listener added to an {@link AuthenticationManager} when the authentication state changes
         *
         * @param userName {@link String} The user's name, if there's one authenticated, null if not.
         */
        void onUserAuthenticated(@Nullable String userName);

    }

}
