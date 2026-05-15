package com.example.tunes_alive_wm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MusicShareAdapter extends RecyclerView.Adapter<MusicShareAdapter.ShareViewHolder> {

    private List<MusicShare> musicShareList;

    public MusicShareAdapter(List<MusicShare> musicShareList) {
        this.musicShareList = musicShareList;
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_share, parent, false);
        return new ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareViewHolder holder, int position) {
        MusicShare currentShare = musicShareList.get(position);

        holder.titleText.setText(currentShare.getSongTitle());
        holder.artistText.setText(currentShare.getArtistName());
        holder.usernameText.setText(currentShare.getUsername());
    }

    @Override
    public int getItemCount() {
        return musicShareList.size();
    }

    public static class ShareViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, artistText, usernameText;

        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_item_title);
            artistText = itemView.findViewById(R.id.tv_item_artist);
            usernameText = itemView.findViewById(R.id.tv_item_username);
        }
    }
}