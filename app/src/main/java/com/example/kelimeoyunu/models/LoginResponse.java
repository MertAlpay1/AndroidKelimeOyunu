package com.example.kelimeoyunu.models;

public class LoginResponse {
    private String status;
    private String message;

    private String username;

    private String totalGames;

    private String wonGames;

    private String userjson;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getTotalGames() {
        return totalGames;
    }

    public String getWonGames() {
        return wonGames;
    }

    public String getUserjson() {
        return userjson;
    }
}