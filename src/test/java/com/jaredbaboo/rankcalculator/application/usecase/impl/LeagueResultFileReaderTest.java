package com.jaredbaboo.rankcalculator.application.usecase.impl;

import com.jaredbaboo.rankcalculator.application.port.in.DataIngestor;
import com.jaredbaboo.rankcalculator.domain.model.MatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeagueResultFileReaderTest {

    @Mock
    private DataIngestor dataIngestor;

    private LeagueResultFileReader leagueResultFileReader;

    @BeforeEach
    void setUp() {
        leagueResultFileReader = new LeagueResultFileReader(dataIngestor);
    }

    @Test
    void shouldLoadSingleMatchResultFromFile() throws IOException {
        String input = "Team A 3, Team B 1";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).homeTeam()).isEqualTo("Team A");
        assertThat(results.get(0).awayTeam()).isEqualTo("Team B");
        assertThat(results.get(0).homePoints()).isEqualTo(3);
        assertThat(results.get(0).awayPoints()).isEqualTo(1);
    }

    @Test
    void shouldLoadMultipleMatchResultsFromFile() throws IOException {
        String input = "Team A 3, Team B 1\nTeam C 2, Team D 0";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).homeTeam()).isEqualTo("Team A");
        assertThat(results.get(0).awayTeam()).isEqualTo("Team B");
        assertThat(results.get(0).homePoints()).isEqualTo(3);
        assertThat(results.get(0).awayPoints()).isEqualTo(1);
        assertThat(results.get(1).homeTeam()).isEqualTo("Team C");
        assertThat(results.get(1).awayTeam()).isEqualTo("Team D");
        assertThat(results.get(1).homePoints()).isEqualTo(2);
        assertThat(results.get(1).awayPoints()).isEqualTo(0);
    }

    @Test
    void shouldHandleTeamNamesWithMultipleWords() throws IOException {
        String input = "Real Madrid 2, FC Barcelona 1";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).homeTeam()).isEqualTo("Real Madrid");
        assertThat(results.get(0).awayTeam()).isEqualTo("FC Barcelona");
        assertThat(results.get(0).homePoints()).isEqualTo(2);
        assertThat(results.get(0).awayPoints()).isEqualTo(1);
    }

    @Test
    void shouldSkipEmptyLines() throws IOException {
        String input = "Team A 3, Team B 1\n\nTeam C 2, Team D 0";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).hasSize(2);
    }

    @Test
    void shouldSkipBlankLines() throws IOException {
        String input = "Team A 3, Team B 1\n   \nTeam C 2, Team D 0";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).hasSize(2);
    }

    @Test
    void shouldThrowExceptionForInvalidScoreNonNumeric() throws IOException {
        String input = "Team A abc, Team B 1";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        assertThatThrownBy(() -> leagueResultFileReader.loadLeagueResults("test.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid score: abc");
    }

    @Test
    void shouldThrowExceptionForNegativeScore() throws IOException {
        String input = "Team A -1, Team B 1";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        assertThatThrownBy(() -> leagueResultFileReader.loadLeagueResults("test.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid score: -1");
    }

    @Test
    void shouldThrowExceptionForScoreTooLarge() throws IOException {
        String input = "Team A 2147483648, Team B 1"; // Integer.MAX_VALUE + 1
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        assertThatThrownBy(() -> leagueResultFileReader.loadLeagueResults("test.txt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Score is out of range");
    }

    @Test
    void shouldThrowExceptionForInvalidLineFormatNotTwoParts() throws IOException {
        String input = "Team A 3";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).isEmpty();
    }

    @Test
    void shouldThrowExceptionForInvalidLineFormatNoScoreInHome() throws IOException {
        String input = "Team A, Team B 1";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        assertThatThrownBy(() -> leagueResultFileReader.loadLeagueResults("test.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid score: A");
    }

    @Test
    void shouldThrowExceptionForInvalidLineFormatNoScoreInAway() throws IOException {
        String input = "Team A 3, Team B";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        assertThatThrownBy(() -> leagueResultFileReader.loadLeagueResults("test.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid score: B");
    }

    @Test
    void shouldReturnEmptyListForEmptyFile() throws IOException {
        String input = "";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        when(dataIngestor.readData("test.txt")).thenReturn(reader);

        List<MatchResult> results = leagueResultFileReader.loadLeagueResults("test.txt");

        assertThat(results).isEmpty();
    }
}
