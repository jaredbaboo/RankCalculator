package com.jaredbaboo.rankcalculator.application.usecase.impl;

import com.jaredbaboo.rankcalculator.application.port.in.DataIngestor;
import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultLoader;
import com.jaredbaboo.rankcalculator.domain.model.MatchResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LeagueResultFileReader implements LeagueResultLoader<MatchResult> {

    private final DataIngestor dataIngestor;

    @Override
    public List<MatchResult> loadLeagueResults(String source) {
        List<MatchResult> results = new ArrayList<>();
        try (BufferedReader reader = dataIngestor.readData(source)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String[] home = parts[0].trim().split("\\s+");
                        String[] away = parts[1].trim().split("\\s+");
                        if (home.length >= 2 && away.length >= 2) {
                            String homeTeam = String.join(" ", java.util.Arrays.copyOfRange(home, 0, home.length - 1));
                            int homeScore = getScore(home[home.length - 1]);
                            String awayTeam = String.join(" ", java.util.Arrays.copyOfRange(away, 0, away.length - 1));
                            int awayScore = getScore(away[away.length - 1]);
                            results.add(new MatchResult(homeTeam, awayTeam, homeScore, awayScore));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
        return results;
    }

    private int getScore(String score) {
        if (validateScore(score)) {
            return Integer.parseInt(score);
        } else {
            throw new IllegalArgumentException("Invalid score: " + score);
        }
    }

    private boolean validateScore(String score) {
        if (StringUtils.isNotBlank(score) && StringUtils.isNumeric(score)) {
            if (new BigInteger(score).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 && Integer.parseInt(score) >= 0) {
                return true;
            } else {
                throw new RuntimeException("Score is out of range");
            }
        }
        return false;
    }

    @Override
    public List<MatchResult> loadLeagueResults() {
        return List.of();
    }
}
