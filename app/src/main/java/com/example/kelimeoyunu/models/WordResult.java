package com.example.kelimeoyunu.models;

import java.util.List;

public class WordResult {

    private String word;
    private int point;

    private List<PlacedLetter> letters;



    public WordResult(String word, int point, List<PlacedLetter> letters) {
        this.word = word;
        this.point = point;
        this.letters = letters;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public List<PlacedLetter> getLetters() {
        return letters;
    }

    public void setLetters(List<PlacedLetter> letters) {
        this.letters = letters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordResult)) return false;
        WordResult that = (WordResult) o;
        return word.equals(that.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
}

