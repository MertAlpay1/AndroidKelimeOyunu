package com.example.kelimeoyunu.models;

public class User {

    private long id;
    private String username;
    private String password;
    private String email;

    private int totalGames;

    private int wonGames;

    public User(long id, String username, String password, String email, int totalGames, int wonGames) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.totalGames = totalGames;
        this.wonGames = wonGames;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.email = null;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User() {

    }

    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getWonGames() {
        return wonGames;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public void setWonGames(int wonGames) {
        this.wonGames = wonGames;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
