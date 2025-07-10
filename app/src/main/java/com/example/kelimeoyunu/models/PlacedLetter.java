package com.example.kelimeoyunu.models;

public class PlacedLetter {
    private int row;
    private int col;
    private String letter;
    private int point;

    public PlacedLetter(int row, int col, String letter,int point) {
        this.row = row;
        this.col = col;
        this.letter = letter;
        this.point=point;
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getLetter() {
        return letter;
    }

    public int getPoint() {
        return point;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
