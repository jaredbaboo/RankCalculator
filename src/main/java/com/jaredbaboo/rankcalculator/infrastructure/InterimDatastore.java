package com.jaredbaboo.rankcalculator.infrastructure;

import com.jaredbaboo.rankcalculator.domain.model.LeagueTable;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class InterimDatastore {
    private LeagueTable leagueTable;
}
