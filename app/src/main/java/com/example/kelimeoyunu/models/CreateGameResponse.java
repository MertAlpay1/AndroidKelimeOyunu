package com.example.kelimeoyunu.models;

public class CreateGameResponse {
    private String message;
    private Game game;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
}
