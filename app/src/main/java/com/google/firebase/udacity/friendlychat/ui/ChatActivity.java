/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.friendlychat.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.android.ActivityScope;
import com.google.firebase.udacity.friendlychat.android.FriendlyChatApplication;
import com.google.firebase.udacity.friendlychat.android.MainComponent;
import com.google.firebase.udacity.friendlychat.domain.model.FriendlyMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ChatActivity extends AppCompatActivity implements ChatActivityPresenter.View {

    private static final int MAIN_ACTIVITY_RC = 1000;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 140;
    private static final int RC_SIGN_IN = MAIN_ACTIVITY_RC + 1;
    private static final int RC_PHOTO_PICKER = MAIN_ACTIVITY_RC + 2;
    private static final int RC_READ_EXT_STORAGE_PERMISSION = MAIN_ACTIVITY_RC + 3;

    private MessageAdapter mMessageAdapter;

    @Inject
    ChatActivityPresenter presenter;

    @BindView(R.id.messageListView)
    ListView mMessageListView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.photoPickerButton)
    ImageButton mPhotoPickerButton;
    @BindView(R.id.messageEditText)
    EditText mMessageEditText;
    @BindView(R.id.sendButton)
    Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        DaggerChatActivity_Component.builder()
                .mainComponent(((FriendlyChatApplication) getApplication()).getComponent())
                .build()
                .inject(this);

        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach(this);
    }

    @Override
    protected void onPause() {
        presenter.detach(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                presenter.sendPhoto(photoUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {

        if (requestCode == RC_READ_EXT_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPhotoPicker();
            }
        }
    }

    @OnClick(R.id.photoPickerButton)
    public void pickPhotoClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    RC_READ_EXT_STORAGE_PERMISSION);
        } else {
            launchPhotoPicker();
        }
    }

    @OnTextChanged(R.id.messageEditText)
    public void notifyTextChanged(CharSequence charSequence) {
        mSendButton.setEnabled(charSequence.toString().trim().length() > 0);
    }

    @OnClick(R.id.sendButton)
    public void send() {
        presenter.sendMessage(mMessageEditText.getText().toString());
        // Clear input box
        mMessageEditText.setText("");
    }

    private void launchPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                RC_PHOTO_PICKER);
    }

    @Override
    public void clearChat() {
        mMessageAdapter.clear();
    }

    @Override
    public void addMessage(final FriendlyMessage message) {
        mMessageAdapter.add(message);
    }

    @Override
    public void showMessages(final List<FriendlyMessage> messages) {
        mMessageAdapter.clear();
        mMessageAdapter.addAll(messages);
    }

    @Override
    public void showAuthenticated(final String userName) {
        Toast.makeText(this, "Signed in as " + userName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotAuthenticated() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @ActivityScope
    @dagger.Component(dependencies = MainComponent.class)
    public interface Component {
        void inject(ChatActivity activity);
    }

}
