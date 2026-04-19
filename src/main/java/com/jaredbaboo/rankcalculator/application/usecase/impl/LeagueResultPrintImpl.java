package com.jaredbaboo.rankcalculator.application.usecase.impl;

import com.jaredbaboo.rankcalculator.application.usecase.LeagueResultPrint;
import com.jaredbaboo.rankcalculator.domain.model.LeagueTable;
import com.jaredbaboo.rankcalculator.domain.model.LeagueTeam;
import com.jaredbaboo.rankcalculator.infrastructure.InterimDatastore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LeagueResultPrintImpl implements LeagueResultPrint {

    private final InterimDatastore datastore;

    @Override
    public void printToConsole() {
        LeagueTable table = datastore.getLeagueTable();
        if (table != null && table.teams() != null) {
            int rank = 1;
            for (int i = 0; i < table.teams().size(); i++) {
                LeagueTeam team = table.teams().get(i);
                if (i > 0 && team.points() < table.teams().get(i-1).points()) {
                    rank = i + 1;
                }
                String pts = team.points() == 1 ? "pt" : "pts";
                System.out.println(rank + ". " + team.name() + ", " + team.points() + " " + pts);
            }
        }
    }

    @Override
    public void printToFile(String filename) {
        LeagueTable table = datastore.getLeagueTable();
        if (table != null && table.teams() != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
                int rank = 1;
                for (int i = 0; i < table.teams().size(); i++) {
                    LeagueTeam team = table.teams().get(i);
                    if (i > 0 && team.points() < table.teams().get(i-1).points()) {
                        rank = i + 1;
                    }
                    String pts = team.points() == 1 ? "pt" : "pts";
                    writer.println(rank + ". " + team.name() + ", " + team.points() + " " + pts);
                }
                System.out.println("Results saved to " + filename);
            } catch (java.io.IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }
    }
}
