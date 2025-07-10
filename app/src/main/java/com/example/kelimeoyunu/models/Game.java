package com.example.kelimeoyunu.models;

public class Game {
    private Long id;
    private User player1;
    private User player2;
    private String selectedDuration;
    private int duration;
    private User currentPlayer;
    private int moveTimeLeft;
    private int player1Point;
    private int player2Point;
    private String winner;
    private String gameState;

    private int remainingLetters;

    private String letterBagJson;
    private String player1LettersJson;
    private String player2LettersJson;
    private String placedLettersJson;

    private String mineJson;

    private String rewardJson;

    private String bannedAreaPlayer;

    private String bannedArea;

    private String frozenLetterPlayer;

    private String frozenLetterIndexes;

    private int player1PassCount;

    private int player2PassCount;

    private boolean updateGamePlayer1;

    private boolean updateGamePlayer2;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public String getSelectedDuration() {
        return selectedDuration;
    }

    public void setSelectedDuration(String selectedDuration) {
        this.selectedDuration = selectedDuration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public User getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(User currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getMoveTimeLeft() {
        return moveTimeLeft;
    }

    public void setMoveTimeLeft(int moveTimeLeft) {
        this.moveTimeLeft = moveTimeLeft;
    }

    public int getPlayer1Point() {
        return player1Point;
    }

    public void setPlayer1Point(int player1Point) {
        this.player1Point = player1Point;
    }

    public int getPlayer2Point() {
        return player2Point;
    }

    public void setPlayer2Point(int player2Point) {
        this.player2Point = player2Point;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public int getRemainingLetters() {
        return remainingLetters;
    }

    public void setRemainingLetters(int remainingLetters) {
        this.remainingLetters = remainingLetters;
    }

    public String getLetterBagJson() {
        return letterBagJson;
    }

    public void setLetterBagJson(String letterBagJson) {
        this.letterBagJson = letterBagJson;
    }

    public String getPlayer1LettersJson() {
        return player1LettersJson;
    }

    public void setPlayer1LettersJson(String player1LettersJson) {
        this.player1LettersJson = player1LettersJson;
    }

    public String getPlayer2LettersJson() {
        return player2LettersJson;
    }

    public void setPlayer2LettersJson(String player2LettersJson) {
        this.player2LettersJson = player2LettersJson;
    }

    public String getPlacedLettersJson() {
        return placedLettersJson;
    }

    public void setPlacedLettersJson(String placedLettersJson) {
        this.placedLettersJson = placedLettersJson;
    }

    public String getMineJson() {
        return mineJson;
    }

    public void setMineJson(String mineJson) {
        this.mineJson = mineJson;
    }

    public String getRewardJson() {
        return rewardJson;
    }

    public void setRewardJson(String rewardJson) {
        this.rewardJson = rewardJson;
    }

    public String getBannedAreaPlayer() {
        return bannedAreaPlayer;
    }

    public void setBannedAreaPlayer(String bannedAreaPlayer) {
        this.bannedAreaPlayer = bannedAreaPlayer;
    }

    public String getBannedArea() {
        return bannedArea;
    }

    public void setBannedArea(String bannedArea) {
        this.bannedArea = bannedArea;
    }

    public String getFrozenLetterPlayer() {
        return frozenLetterPlayer;
    }

    public void setFrozenLetterPlayer(String frozenLetterPlayer) {
        this.frozenLetterPlayer = frozenLetterPlayer;
    }

    public String getFrozenLetterIndexes() {
        return frozenLetterIndexes;
    }

    public void setFrozenLetterIndexes(String frozenLetterIndexes) {
        this.frozenLetterIndexes = frozenLetterIndexes;
    }

    public int getPlayer2PassCount() {
        return player2PassCount;
    }

    public void setPlayer2PassCount(int player2PassCount) {
        this.player2PassCount = player2PassCount;
    }

    public int getPlayer1PassCount() {
        return player1PassCount;
    }

    public void setPlayer1PassCount(int player1PassCount) {
        this.player1PassCount = player1PassCount;
    }

    public boolean isUpdateGamePlayer1() {
        return updateGamePlayer1;
    }

    public void setUpdateGamePlayer1(boolean updateGamePlayer1) {
        this.updateGamePlayer1 = updateGamePlayer1;
    }

    public boolean isUpdateGamePlayer2() {
        return updateGamePlayer2;
    }

    public void setUpdateGamePlayer2(boolean updateGamePlayer2) {
        this.updateGamePlayer2 = updateGamePlayer2;
    }



}
