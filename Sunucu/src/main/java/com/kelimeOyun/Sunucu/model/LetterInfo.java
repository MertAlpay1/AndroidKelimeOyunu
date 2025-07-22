
package com.kelimeOyun.Sunucu.model;

public class LetterInfo {
    private int count;   
    private int point;  

    public LetterInfo(int count, int point) {
        this.count = count;
        this.point = point;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
