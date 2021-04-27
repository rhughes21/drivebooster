package com.personal.drivebooster;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManoeuvresAdapter extends RecyclerView.Adapter {

    List<Manoeuvres> manoeuvres;
    Lifecycle lifecycle;

    int sizeOfView;

    public ManoeuvresAdapter(List<Manoeuvres> manoeuvres, Lifecycle lifecycle){
        this.manoeuvres = manoeuvres;
        this.lifecycle = lifecycle;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manoeuvre_video_card, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        viewHolder myViewHolder=(viewHolder)holder;
        sizeOfView = sizeOfView +1;
        myViewHolder.title.setText((CharSequence) manoeuvres.get(position).title);
        myViewHolder.youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(manoeuvres.get(position).videoId, 0);
            }
        });
        lifecycle.addObserver(myViewHolder.youTubePlayerView);
    }

    @Override
    public int getItemCount() {
        return manoeuvres.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView title;
        YouTubePlayerView youTubePlayerView;
        CardView cardViewLayout;
        ConstraintLayout constraintLayout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            youTubePlayerView = itemView.findViewById(R.id.youtube_player);
            cardViewLayout = itemView.findViewById(R.id.cardview_layout);
            constraintLayout = itemView.findViewById(R.id.manoeuvres_constraint_layout);
        }
    }
}


