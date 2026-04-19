package com.jaredbaboo.rankcalculator;

import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultLoader;
import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultPrint;
import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultsCalculator;
import com.jaredbaboo.rankcalculator.domain.exceptions.InputFileNotFoundException;
import com.jaredbaboo.rankcalculator.domain.model.MatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
@Profile("!test")
public class LeagueRunner implements CommandLineRunner {

    @Autowired
    private LeagueResultLoader<MatchResult> leagueResultLoader;

    @Autowired
    private LeagueResultsCalculator leagueResultsCalculator;

    @Autowired
    private LeagueResultPrint leagueResultPrint;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("1. Load league results from a file");
        System.out.println("2. Manually enter match results");
        System.out.println("3. Cancel and exit");
        int choice = 0;
        try {
            choice = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Exiting.");
            return;
        }
        scanner.nextLine(); // consume newline

        List<MatchResult> matchResults = new ArrayList<>();

        if (choice == 1) {
            matchResults = handleFileInput(scanner);
        } else if (choice == 2) {
            handleManualInputs(scanner, matchResults);
        } else if (choice == 3) {
            System.out.println("Exiting application.");
            return;
        } else {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        leagueResultsCalculator.calculateLeagueStandings(matchResults);
        leagueResultPrint.printToConsole();

        if( matchResults != null && !matchResults.isEmpty()) {
            handleLeagueResults(scanner);
        } else{
            System.out.println("No valid results loaded. Exiting.");
        }
    }

    private static void handleManualInputs(Scanner scanner, List<MatchResult> matchResults) {
        System.out.println("Enter match results (format: TeamA score, TeamB score). Enter 'done' to finish:");
        String line;
        while (!(line = scanner.nextLine()).equals("done")) {
            if (!line.trim().isEmpty()) {
                // Parse the line similar to file parsing
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String[] home = parts[0].trim().split("\\s+");
                    String[] away = parts[1].trim().split("\\s+");
                    if (home.length >= 2 && away.length >= 2) {
                        processInput(matchResults, home, away, line);
                    } else {
                        System.out.println("Invalid format: " + line + " - must be in the format TeamA score, TeamB score");
                    }
                } else {
                    System.out.println("Invalid format: " + line + " - must be in the format TeamA score, TeamB score");
                }
            }
        }
    }

    private static void processInput(List<MatchResult> matchResults, String[] home, String[] away, String line) {
        try {
            String homeTeam = String.join(" ", Arrays.copyOfRange(home, 0, home.length - 1));
            int homeScore = Integer.parseInt(home[home.length - 1]);
            String awayTeam = String.join(" ", Arrays.copyOfRange(away, 0, away.length - 1));
            int awayScore = Integer.parseInt(away[away.length - 1]);
            matchResults.add(new MatchResult(homeTeam, awayTeam, homeScore, awayScore));
        } catch (NumberFormatException e) {
            System.out.println("Invalid score format in: " + line);
        }
    }

    private void handleLeagueResults(Scanner scanner) {
        System.out.println("Do you want to save the results to a file? (y/n)");
        String saveChoice = scanner.nextLine().trim().toLowerCase();
        if (saveChoice.equals("y") || saveChoice.equals("yes")) {
            System.out.println("Enter the filename to save results:");
            String outputFilename = scanner.nextLine();
            leagueResultPrint.printToFile(outputFilename);
        }
    }

    private List<MatchResult> handleFileInput(Scanner scanner) {
        List<MatchResult> matchResults = List.of();
        System.out.println("Enter the filename:");
        String filename = scanner.nextLine();
        try {
            matchResults = leagueResultLoader.loadLeagueResults(filename);
        } catch (InputFileNotFoundException e) {
            System.out.println("Invalid filename. Exiting.");
        }
        return matchResults;
    }
}
