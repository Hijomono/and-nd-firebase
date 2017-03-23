package com.google.firebase.udacity.friendlychat.data;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.udacity.friendlychat.android.authentication.AuthenticationManager;
import com.google.firebase.udacity.friendlychat.domain.model.FriendlyMessage;
import com.google.firebase.udacity.friendlychat.domain.repository.ChatRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FirebaseChatStorage implements ChatRepository {

    private static final String CHAT_DATABASE_REFERENCE = "messages";
    private static final String PHOTOS_DATABASE_REFERENCE = "photos";

    private final DatabaseReference chatDatabase;
    private final ChildEventListener childEventListener;
    private final StorageReference photosDatabase;
    private final List<ChatListener> listeners;
    private final List<FriendlyMessage> messages;
    private final Set<String> messagesKeys;

    private String userName;

    public FirebaseChatStorage(final AuthenticationManager authenticationManager) {
        listeners = new ArrayList<>();
        messages = new ArrayList<>();
        messagesKeys = new HashSet<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
                synchronized (listeners) {
                    if (!messagesKeys.contains(dataSnapshot.getKey())) {
                        messagesKeys.add(dataSnapshot.getKey());
                        final FriendlyMessage message = dataSnapshot.getValue(FriendlyMessage.class);
                        messages.add(message);
                        for (final ChatListener listener : listeners) {
                            listener.onMessageAdded(message);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {

            }

            @Override
            public void onChildRemoved(final DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {

            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {

            }
        };
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setLogLevel(Logger.Level.DEBUG);
        chatDatabase = firebaseDatabase.getReference(CHAT_DATABASE_REFERENCE);
        photosDatabase = FirebaseStorage.getInstance().getReference(PHOTOS_DATABASE_REFERENCE);
        authenticationManager.addAuthenticationChangedListener(new AuthenticationManager.AuthenticationChangedListener() {
            @Override
            public void onUserAuthenticated(@Nullable final String userName) {
                FirebaseChatStorage.this.userName = userName;
                if (userName == null) {
                    onAuthenticationLost();
                } else {
                    onAuthentication();
                }
            }
        });
    }

    @Override
    public void addChatListener(final ChatListener listener) {
        synchronized (listeners) {
            listener.onChatLoaded(messages);
            listeners.add(listener);
        }
    }

    @Override
    public void removeChatListener(final ChatListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void addMessage(final String message) {
        chatDatabase.push().setValue(new FriendlyMessage(message, userName, null));
    }

    @Override
    public void addPhoto(final Uri photoUri) {
        StorageReference photoReference = photosDatabase.child(photoUri.getLastPathSegment());
        photoReference.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                chatDatabase.push().setValue(new FriendlyMessage(null, userName, taskSnapshot.getDownloadUrl().toString()));
            }

        });
    }

    private void onAuthentication() {
        chatDatabase.addChildEventListener(childEventListener);
    }

    private void onAuthenticationLost() {
        chatDatabase.removeEventListener(childEventListener);
        clearChat();
    }

    private void clearChat() {
        synchronized (listeners) {
            messages.clear();
            messagesKeys.clear();
            for (final ChatListener listener : listeners) {
                listener.onChatCleared();
            }
        }
    }

}
