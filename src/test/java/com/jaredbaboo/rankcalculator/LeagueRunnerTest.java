package com.jaredbaboo.rankcalculator;

import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultLoader;
import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultPrint;
import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultsCalculator;
import com.jaredbaboo.rankcalculator.domain.exceptions.InputFileNotFoundException;
import com.jaredbaboo.rankcalculator.domain.model.MatchResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeagueRunnerTest {

    @Mock
    private LeagueResultLoader<MatchResult> leagueResultLoader;

    @Mock
    private LeagueResultsCalculator leagueResultsCalculator;

    @Mock
    private LeagueResultPrint leagueResultPrint;

    @InjectMocks
    private LeagueRunner leagueRunner;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void shouldLoadLeagueResultsFromFileWhenOptionOne() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultLoader).loadLeagueResults("test.txt");
        verify(leagueResultsCalculator).calculateLeagueStandings(mockResults);
        verify(leagueResultPrint).printToConsole();
    }

    @Test
    void shouldDisplayMenuWithThreeOptions() throws Exception {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Choose an option:");
        assertThat(output).contains("1. Load league results from a file");
        assertThat(output).contains("2. Manually enter match results");
        assertThat(output).contains("3. Cancel and exit");
    }

    @Test
    void shouldExitApplicationWhenOptionThree() throws Exception {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Exiting application.");
        verify(leagueResultsCalculator, never()).calculateLeagueStandings(any());
        verify(leagueResultPrint, never()).printToConsole();
    }

    @Test
    void shouldHandleInvalidMenuChoice() throws Exception {
        String input = "5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid choice. Exiting.");
        verify(leagueResultsCalculator, never()).calculateLeagueStandings(any());
    }

    @Test
    void shouldHandleInvalidInputForMenuSelection() throws Exception {
        String input = "abc\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid input. Exiting.");
        verify(leagueResultsCalculator, never()).calculateLeagueStandings(any());
    }

    @Test
    void shouldAcceptManualMatchResultsWhenOptionTwo() throws Exception {
        String input = "2\nTeam A 3, Team B 1\nTeam C 2, Team D 0\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Enter match results");
        verify(leagueResultsCalculator).calculateLeagueStandings(any());
        verify(leagueResultPrint).printToConsole();
    }

    @Test
    void shouldSkipEmptyLinesInManualInput() throws Exception {
        String input = "2\n\nTeam A 3, Team B 1\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultsCalculator).calculateLeagueStandings(any());
    }

    @Test
    void shouldRejectManualInputWithInvalidFormat() throws Exception {
        String input = "2\nInvalid format\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid format:");
    }

    @Test
    void shouldRejectManualInputWithOnlyTeamName() throws Exception {
        String input = "2\nTeam A 3\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid format:");
    }

    @Test
    void shouldRejectManualInputWithNonNumericScore() throws Exception {
        String input = "2\nTeam A abc, Team B 1\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid score format");
    }

    @Test
    void shouldHandleManualInputWithMinimumValidFormat() throws Exception {
        String input = "2\nA 3, B 1\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultsCalculator).calculateLeagueStandings(any());
    }

    @Test
    void shouldSaveResultsToFileWhenUserChoosesYes() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\ny\noutput.txt\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultPrint).printToFile("output.txt");
    }

    @Test
    void shouldNotSaveResultsToFileWhenUserChoosesNo() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultPrint, never()).printToFile(anyString());
    }

    @Test
    void shouldHandleFileNotFoundExceptionWhenLoadingResults() throws Exception {
        when(leagueResultLoader.loadLeagueResults("nonexistent.txt"))
                .thenThrow(new InputFileNotFoundException("File not found", new java.io.IOException()));

        String input = "1\nnonexistent.txt\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid filename. Exiting.");
    }

    @Test
    void shouldNotCalculateStandingsWhenNoResultsLoaded() throws Exception {
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(List.of());

        String input = "1\ntest.txt\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("No valid results loaded. Exiting.");
        verify(leagueResultsCalculator).calculateLeagueStandings(List.of());
    }

    @Test
    void shouldHandleYesResponseForSavingResults() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\nyes\nresults.txt\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultPrint).printToFile("results.txt");
    }

    @Test
    void shouldNotSaveResultsWhenUserRespondsWithLowercaseNo() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultPrint, never()).printToFile(anyString());
    }

    @Test
    void shouldHandleMultipleManualEntries() throws Exception {
        String input = "2\nTeam A 3, Team B 1\nTeam C 2, Team D 0\nTeam E 1, Team F 1\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultsCalculator).calculateLeagueStandings(any());
        verify(leagueResultPrint).printToConsole();
    }

    @Test
    void shouldHandleTeamNamesWithMultipleWords() throws Exception {
        String input = "2\nReal Madrid 3, FC Barcelona 1\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultsCalculator).calculateLeagueStandings(any());
    }

    @Test
    void shouldRejectMatchWithMissingCommaInManualInput() throws Exception {
        String input = "2\nTeam A 3 Team B 1\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Invalid format:");
    }

    @Test
    void shouldPromptForFilenameWhenSavingResults() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\ny\noutput.txt\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Enter the filename to save results:");
    }

    @Test
    void shouldCalculateAndPrintStandingsAfterLoadingResults() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        verify(leagueResultsCalculator).calculateLeagueStandings(mockResults);
        verify(leagueResultPrint).printToConsole();
    }

    @Test
    void shouldHandleEmptyManualInput() throws Exception {
        String input = "2\ndone\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("No valid results loaded. Exiting.");
    }

    @Test
    void shouldPromptForFilenameWhenLoadingResults() throws Exception {
        List<MatchResult> mockResults = List.of(
                new MatchResult("Team A", "Team B", 3, 1)
        );
        when(leagueResultLoader.loadLeagueResults("test.txt")).thenReturn(mockResults);

        String input = "1\ntest.txt\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        leagueRunner.run();

        String output = outputStream.toString();
        assertThat(output).contains("Enter the filename:");
    }
}

