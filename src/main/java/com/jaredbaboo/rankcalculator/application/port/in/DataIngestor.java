package com.jaredbaboo.rankcalculator.application.port.in;

import java.io.BufferedReader;

public interface DataIngestor {

    BufferedReader readData(String source);
}
