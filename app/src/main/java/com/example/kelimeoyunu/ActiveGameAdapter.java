package com.example.kelimeoyunu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelimeoyunu.models.Game;

import java.util.List;

public class ActiveGameAdapter extends RecyclerView.Adapter<ActiveGameAdapter.GameViewHolder> {

    private List<Game> gameList;
    private Context context;
    private OnGameClickListener onGameClickListener;

    public ActiveGameAdapter(Context context, List<Game> gameList, OnGameClickListener listener) {
        this.context = context;
        this.gameList = gameList;
        this.onGameClickListener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.active_game_item, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);

        holder.player1Text.setText("Player1 :"+game.getPlayer1().getUsername() + " (Puan: " + game.getPlayer1Point() + ")");
        holder.player2Text.setText("Player2 :"+game.getPlayer2().getUsername() + " (Puan: " + game.getPlayer2Point() + ")");

        holder.gameStatusText.setText("Sıra: " + game.getCurrentPlayer().getUsername());

        holder.itemView.setOnClickListener(v -> onGameClickListener.onGameClick(game));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView player1Text, player2Text, gameStatusText;

        public GameViewHolder(View itemView) {
            super(itemView);
            player1Text = itemView.findViewById(R.id.player1);
            player2Text = itemView.findViewById(R.id.player2);
            gameStatusText = itemView.findViewById(R.id.game_status);
        }
    }

    public interface OnGameClickListener {
        void onGameClick(Game game);
    }
}
