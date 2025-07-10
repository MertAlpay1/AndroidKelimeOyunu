package com.example.kelimeoyunu.models;

public class Letters {

    private String letter;

    private LetterInfo letterInfo;

    public Letters(String letter, LetterInfo letterInfo) {
        this.letter = letter;
        this.letterInfo = letterInfo;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public LetterInfo getLetterInfo() {
        return letterInfo;
    }

    public void setLetterInfo(LetterInfo letterInfo) {
        this.letterInfo = letterInfo;
    }
}
