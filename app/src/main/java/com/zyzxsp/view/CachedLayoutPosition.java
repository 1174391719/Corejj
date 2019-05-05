package com.zyzxsp.view;

public class CachedLayoutPosition {
    private int l;
    private int t;
    private int r;
    private int b;

    public CachedLayoutPosition(int l, int t, int r, int b) {
        super();
        this.l = l;
        this.t = t;
        this.r = r;
        this.b = b;
    }

    public void setVals(int l, int t, int r, int b) {
        this.l = l;
        this.t = t;
        this.r = r;
        this.b = b;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }

    public int getR() {
        return r;
    }

    public int getB() {
        return b;
    }
}
