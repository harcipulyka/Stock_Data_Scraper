package com.company;

public class Pair {
    public static final Pair WRONG_PAIR = new Pair("NaN", Integer.MIN_VALUE);

    private final String s;
    private final Integer i;
    private final Double d;
    private boolean gultig;

    public Pair(String s, Integer i) {
        this.s = s;
        this.i = i;
        this.d = Double.MIN_VALUE;
        gultig = !s.equals("NaN");
    }

    public Pair(String s, Double d) {
        this.s = s;
        this.d = d;
        this.i = Integer.MIN_VALUE;
    }

    public String getString() {
        return s;
    }

    public Integer getInteger() {
        return i;
    }

    public Double getDouble() {
        return d;
    }

    public boolean correct() {
        return gultig;
    }
}
