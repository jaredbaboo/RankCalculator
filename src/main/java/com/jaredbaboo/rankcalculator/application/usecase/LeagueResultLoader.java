package com.jaredbaboo.rankcalculator.application.usecase;

import java.util.List;

public interface LeagueResultLoader<T> {
    List<T> loadLeagueResults(String source);

    List<T> loadLeagueResults();
}
