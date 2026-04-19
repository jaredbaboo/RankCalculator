package com.jaredbaboo.rankcalculator.domain.model;

public enum Result {
    WIN(3), LOSE(0), DRAW(1);
    int points;
    Result(int points) {
        this.points = points;
    }
    public int getPoints() {
        return points;
    }
}
