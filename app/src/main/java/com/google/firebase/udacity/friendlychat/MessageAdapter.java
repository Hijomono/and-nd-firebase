package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends ArrayAdapter<FriendlyMessage> {
    public MessageAdapter(Context context, int resource, List<FriendlyMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }
        final ViewHolder holder = new ViewHolder(convertView);
        final FriendlyMessage message = getItem(position);

        if (message.getPhotoUrl() != null) {
            holder.message.setVisibility(View.GONE);
            holder.photo.setVisibility(View.VISIBLE);
            Glide.with(holder.photo.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photo);
        } else {
            holder.message.setVisibility(View.VISIBLE);
            holder.photo.setVisibility(View.GONE);
            holder.message.setText(message.getText());
        }
        holder.name.setText(message.getName());

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.photoImageView)
        ImageView photo;
        @BindView(R.id.messageTextView)
        TextView message;
        @BindView(R.id.nameTextView)
        TextView name;

        ViewHolder(final View view) {
            ButterKnife.bind(this, view);
        }

    }

}
