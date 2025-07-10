package com.example.kelimeoyunu;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelimeoyunu.models.Game;

import java.util.List;
public class EndedGamesAdapter extends RecyclerView.Adapter<EndedGamesAdapter.GameViewHolder>{

    private List<Game> games;
    private String loggedInUsername;

    public EndedGamesAdapter(List<Game> games, String loggedInUsername) {
        this.games = games;
        this.loggedInUsername = loggedInUsername;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ended_game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);

        boolean isPlayer1 = game.getPlayer1().getUsername().equals(loggedInUsername);
        int yourScore = isPlayer1 ? game.getPlayer1Point() : game.getPlayer2Point();
        int opponentScore = isPlayer1 ? game.getPlayer2Point() : game.getPlayer1Point();
        boolean isWinner = loggedInUsername.equals(game.getWinner());

        holder.txtYourScore.setText("Sizin Puanınız: " + yourScore);
        holder.txtOpponentScore.setText("Rakip Puanı: " + opponentScore);

        if(game.getWinner().equals("berabere")){
            holder.txtResult.setText("Sonuç: berabere ");
        }else{
            holder.txtResult.setText("Sonuç: " + (isWinner ? "Kazandınız " : "Kaybettiniz "));
        }

    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView  txtYourScore, txtOpponentScore, txtResult;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            txtYourScore = itemView.findViewById(R.id.txtYourScore);
            txtOpponentScore = itemView.findViewById(R.id.txtOpponentScore);
            txtResult = itemView.findViewById(R.id.txtResult);
        }
    }
}
