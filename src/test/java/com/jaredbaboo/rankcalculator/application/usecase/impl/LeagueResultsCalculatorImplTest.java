package com.jaredbaboo.rankcalculator.application.usecase.impl;

import com.jaredbaboo.rankcalculator.domain.model.LeagueTable;
import com.jaredbaboo.rankcalculator.domain.model.LeagueTeam;
import com.jaredbaboo.rankcalculator.domain.model.MatchResult;
import com.jaredbaboo.rankcalculator.infrastructure.InterimDatastore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LeagueResultsCalculatorImplTest {

    @Mock
    private InterimDatastore datastore;

    private LeagueResultsCalculatorImpl calculator;

    @BeforeEach
    void setUp() {
        calculator = new LeagueResultsCalculatorImpl(datastore);
    }

    @Test
    void shouldCalculateStandingsForSingleMatchHomeWin() {
        List<MatchResult> matchResults = List.of(new MatchResult("Team A", "Team B", 3, 1));

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).hasSize(2);
        assertThat(table.teams().get(0).name()).isEqualTo("Team A");
        assertThat(table.teams().get(0).points()).isEqualTo(3);
        assertThat(table.teams().get(1).name()).isEqualTo("Team B");
        assertThat(table.teams().get(1).points()).isEqualTo(0);
    }

    @Test
    void shouldCalculateStandingsForSingleMatchAwayWin() {
        List<MatchResult> matchResults = List.of(new MatchResult("Team A", "Team B", 1, 3));

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).hasSize(2);
        assertThat(table.teams().get(0).name()).isEqualTo("Team B");
        assertThat(table.teams().get(0).points()).isEqualTo(3);
        assertThat(table.teams().get(1).name()).isEqualTo("Team A");
        assertThat(table.teams().get(1).points()).isEqualTo(0);
    }

    @Test
    void shouldCalculateStandingsForSingleMatchDraw() {
        List<MatchResult> matchResults = List.of(new MatchResult("Team A", "Team B", 2, 2));

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).hasSize(2);
        assertThat(table.teams().get(0).name()).isEqualTo("Team A");
        assertThat(table.teams().get(0).points()).isEqualTo(1);
        assertThat(table.teams().get(1).name()).isEqualTo("Team B");
        assertThat(table.teams().get(1).points()).isEqualTo(1);
    }

    @Test
    void shouldCalculateStandingsForMultipleMatches() {
        List<MatchResult> matchResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1),
                new MatchResult("Team A", "Team C", 2, 2),
                new MatchResult("Team B", "Team C", 0, 3)
        );

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).hasSize(3);
        assertThat(table.teams().get(0).name()).isEqualTo("Team A");
        assertThat(table.teams().get(0).points()).isEqualTo(4); // 3 + 1
        assertThat(table.teams().get(1).name()).isEqualTo("Team C");
        assertThat(table.teams().get(1).points()).isEqualTo(4); // 1 + 3
        assertThat(table.teams().get(2).name()).isEqualTo("Team B");
        assertThat(table.teams().get(2).points()).isEqualTo(0); // 0 + 0
    }

    @Test
    void shouldSortByPointsDescendingThenNameAscending() {
        List<MatchResult> matchResults = List.of(
                new MatchResult("Team B", "Team A", 3, 1),
                new MatchResult("Team C", "Team D", 2, 2)
        );

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).hasSize(4);
        assertThat(table.teams().get(0).name()).isEqualTo("Team B");
        assertThat(table.teams().get(0).points()).isEqualTo(3);
        assertThat(table.teams().get(1).name()).isEqualTo("Team C");
        assertThat(table.teams().get(1).points()).isEqualTo(1);
        assertThat(table.teams().get(2).name()).isEqualTo("Team D");
        assertThat(table.teams().get(2).points()).isEqualTo(1);
        assertThat(table.teams().get(3).name()).isEqualTo("Team A");
        assertThat(table.teams().get(3).points()).isEqualTo(0);
    }

    @Test
    void shouldHandleEmptyMatchResults() {
        List<MatchResult> matchResults = List.of();

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).isEmpty();
    }

    @Test
    void shouldAccumulatePointsForSameTeamAcrossMatches() {
        List<MatchResult> matchResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1),
                new MatchResult("Team A", "Team C", 3, 0)
        );

        calculator.calculateLeagueStandings(matchResults);

        ArgumentCaptor<LeagueTable> captor = ArgumentCaptor.forClass(LeagueTable.class);
        verify(datastore).setLeagueTable(captor.capture());
        LeagueTable table = captor.getValue();
        assertThat(table.teams()).hasSize(3);
        assertThat(table.teams().get(0).name()).isEqualTo("Team A");
        assertThat(table.teams().get(0).points()).isEqualTo(6); // 3 + 3
        assertThat(table.teams().get(1).name()).isEqualTo("Team B");
        assertThat(table.teams().get(1).points()).isEqualTo(0);
        assertThat(table.teams().get(2).name()).isEqualTo("Team C");
        assertThat(table.teams().get(2).points()).isEqualTo(0);
    }
}
