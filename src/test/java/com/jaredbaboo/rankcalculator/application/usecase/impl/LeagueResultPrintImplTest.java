package com.jaredbaboo.rankcalculator.application.usecase.impl;

import com.jaredbaboo.rankcalculator.domain.model.LeagueTable;
import com.jaredbaboo.rankcalculator.domain.model.LeagueTeam;
import com.jaredbaboo.rankcalculator.infrastructure.InterimDatastore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeagueResultPrintImplTest {

    @Mock
    private InterimDatastore datastore;

    private LeagueResultPrintImpl printer;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        printer = new LeagueResultPrintImpl(datastore);
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldPrintToConsoleWithTeams() {
        List<LeagueTeam> teams = List.of(
                new LeagueTeam("Team A", 6),
                new LeagueTeam("Team B", 4),
                new LeagueTeam("Team C", 4),
                new LeagueTeam("Team D", 0)
        );
        LeagueTable table = new LeagueTable(teams);
        when(datastore.getLeagueTable()).thenReturn(table);

        printer.printToConsole();

        String output = outputStream.toString();
        assertThat(output).contains("1. Team A, 6 pts");
        assertThat(output).contains("2. Team B, 4 pts");
        assertThat(output).contains("2. Team C, 4 pts");
        assertThat(output).contains("4. Team D, 0 pts");
    }

    @Test
    void shouldPrintToConsoleWithEmptyTable() {
        LeagueTable table = new LeagueTable(List.of());
        when(datastore.getLeagueTable()).thenReturn(table);

        printer.printToConsole();

        String output = outputStream.toString();
        assertThat(output).isEmpty();
    }

    @Test
    void shouldPrintToConsoleWithNullTable() {
        when(datastore.getLeagueTable()).thenReturn(null);

        printer.printToConsole();

        String output = outputStream.toString();
        assertThat(output).isEmpty();
    }

    @Test
    void shouldPrintToConsoleWithSingleTeam() {
        List<LeagueTeam> teams = List.of(new LeagueTeam("Team A", 3));
        LeagueTable table = new LeagueTable(teams);
        when(datastore.getLeagueTable()).thenReturn(table);

        printer.printToConsole();

        String output = outputStream.toString();
        assertThat(output).contains("1. Team A, 3 pts");
    }

    @Test
    void shouldPrintToConsoleWithOnePoint() {
        List<LeagueTeam> teams = List.of(new LeagueTeam("Team A", 1));
        LeagueTable table = new LeagueTable(teams);
        when(datastore.getLeagueTable()).thenReturn(table);

        printer.printToConsole();

        String output = outputStream.toString();
        assertThat(output).contains("1. Team A, 1 pt");
    }

    @Test
    void shouldPrintToFileWithTeams(@TempDir Path tempDir) throws Exception {
        List<LeagueTeam> teams = List.of(
                new LeagueTeam("Team A", 6),
                new LeagueTeam("Team B", 4)
        );
        LeagueTable table = new LeagueTable(teams);
        when(datastore.getLeagueTable()).thenReturn(table);
        Path tempFile = tempDir.resolve("output.txt");

        printer.printToFile(tempFile.toString());

        List<String> lines = Files.readAllLines(tempFile);
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).isEqualTo("1. Team A, 6 pts");
        assertThat(lines.get(1)).isEqualTo("2. Team B, 4 pts");
        String consoleOutput = outputStream.toString();
        assertThat(consoleOutput).contains("Results saved to " + tempFile.toString());
    }

    @Test
    void shouldPrintToFileWithEmptyTable(@TempDir Path tempDir) throws Exception {
        LeagueTable table = new LeagueTable(List.of());
        when(datastore.getLeagueTable()).thenReturn(table);
        Path tempFile = tempDir.resolve("output.txt");

        printer.printToFile(tempFile.toString());

        List<String> lines = Files.readAllLines(tempFile);
        assertThat(lines).isEmpty();
        String consoleOutput = outputStream.toString();
        assertThat(consoleOutput).contains("Results saved to " + tempFile.toString());
    }
}
