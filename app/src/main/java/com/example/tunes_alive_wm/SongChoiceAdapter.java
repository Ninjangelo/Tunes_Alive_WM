package com.example.tunes_alive_wm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class SongChoiceAdapter extends RecyclerView.Adapter<SongChoiceAdapter.SongViewHolder> {

    private List<Track> trackList;
    private final OnSongClickListener clickListener;

    // Interface that bridges back to the Activity
    public interface OnSongClickListener {
        void onSongClick(Track track);
    }

    // Constructor to require the listener
    public SongChoiceAdapter(List<Track> trackList, OnSongClickListener clickListener) {
        this.trackList = trackList;
        this.clickListener = clickListener;
    }

    public void updateTracks(List<Track> newTracks) {
        this.trackList = newTracks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_result, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Track track = trackList.get(position);

        holder.titleText.setText(track.name);
        holder.artistText.setText(track.artist);

        String imageUrl = track.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(android.R.drawable.progress_horizontal)
                    .error(android.R.drawable.ic_media_play)
                    .into(holder.albumArt);
        } else {
            holder.albumArt.setImageResource(android.R.drawable.ic_media_play);
            holder.albumArt.setColorFilter(android.graphics.Color.parseColor("#6200EE"));
        }

        // Enables songs to be clickable
        holder.itemView.setOnClickListener(v -> clickListener.onSongClick(track));
    }

    @Override
    public int getItemCount() {
        return trackList != null ? trackList.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        TextView titleText, artistText;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.iv_album_art);
            titleText = itemView.findViewById(R.id.tv_result_title);
            artistText = itemView.findViewById(R.id.tv_result_artist);
        }
    }
}