package com.jaredbaboo.rankcalculator.domain.model;

public record MatchResult(String homeTeam, String awayTeam, int homePoints, int awayPoints) {
    public Result getHomeResult() {
        if (homePoints > awayPoints) {
            return Result.WIN;
        } else if (homePoints < awayPoints) {
            return Result.LOSE;
        } else {
            return Result.DRAW;
        }
    }

    public Result getAwayResult() {
        if (homePoints > awayPoints) {
            return Result.LOSE;
        } else if (homePoints < awayPoints) {
            return Result.WIN;
        } else {
            return Result.DRAW;
        }
    }
}
