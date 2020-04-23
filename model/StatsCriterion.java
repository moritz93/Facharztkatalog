package com.example.facharztkatalog.model;

public class StatsCriterion {
    public final long id;
    public final String name;
    public final int target;
    public int value;

    public StatsCriterion(long id, String name, int target, int value) {
        this.id = id;
        this.name = name;
        this.target = target;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
