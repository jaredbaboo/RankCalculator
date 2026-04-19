package com.jaredbaboo.rankcalculator.application.port.out;

public interface FileGenerator {
    void createFile(String filePath, String content);
}
