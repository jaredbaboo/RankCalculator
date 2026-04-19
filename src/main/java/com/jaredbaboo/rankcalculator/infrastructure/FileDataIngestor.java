package com.jaredbaboo.rankcalculator.infrastructure;

import com.jaredbaboo.rankcalculator.application.port.in.DataIngestor;
import com.jaredbaboo.rankcalculator.domain.exceptions.InputFileNotFoundException;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class FileDataIngestor implements DataIngestor {

    @Override
    public BufferedReader readData(String source) {
        try {
            return new BufferedReader(new FileReader(source));
        } catch (IOException e) {
            throw new InputFileNotFoundException("Failed to read file: " + source, e);
        }
    }
}
