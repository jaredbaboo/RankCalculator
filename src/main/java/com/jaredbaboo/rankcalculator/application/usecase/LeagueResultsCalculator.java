package com.jaredbaboo.rankcalculator.application.usecase;

import com.jaredbaboo.rankcalculator.domain.model.MatchResult;

import java.util.List;

public interface LeagueResultsCalculator {

    void calculateLeagueStandings(List<MatchResult> matchResults);

}
