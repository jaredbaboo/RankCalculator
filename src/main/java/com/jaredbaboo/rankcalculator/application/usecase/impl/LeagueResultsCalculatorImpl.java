package com.jaredbaboo.rankcalculator.application.usecase.impl;

import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultsCalculator;
import com.jaredbaboo.rankcalculator.domain.model.LeagueTable;
import com.jaredbaboo.rankcalculator.domain.model.LeagueTeam;
import com.jaredbaboo.rankcalculator.domain.model.MatchResult;
import com.jaredbaboo.rankcalculator.infrastructure.InterimDatastore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LeagueResultsCalculatorImpl implements LeagueResultsCalculator {

    private final InterimDatastore datastore;

    @Override
    public void calculateLeagueStandings(List<MatchResult> matchResults) {
        Map<String, Integer> teamPoints = new HashMap<>();

        for (MatchResult result : matchResults) {
            // Home team points
            teamPoints.put(result.homeTeam(), teamPoints.getOrDefault(result.homeTeam(), 0) + result.getHomeResult().getPoints());
            // Away team points
            teamPoints.put(result.awayTeam(), teamPoints.getOrDefault(result.awayTeam(), 0) + result.getAwayResult().getPoints());
        }

        List<LeagueTeam> teams = teamPoints.entrySet().stream()
                .map(entry -> new LeagueTeam(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(LeagueTeam::points).reversed().thenComparing(LeagueTeam::name))
                .collect(Collectors.toList());

        datastore.setLeagueTable(new LeagueTable(teams));
    }
}
