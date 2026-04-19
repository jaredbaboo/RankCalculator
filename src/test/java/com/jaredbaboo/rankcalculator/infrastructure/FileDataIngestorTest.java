package com.jaredbaboo.rankcalculator.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDataIngestorTest {

    private final FileDataIngestor ingestor = new FileDataIngestor();

    @Test
    void shouldReturnBufferedReaderForExistingFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        Files.writeString(tempFile, "test content");

        BufferedReader reader = ingestor.readData(tempFile.toString());

        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isEqualTo("test content");
        reader.close();
    }

    @Test
    void shouldThrowExceptionForNonExistentFile() {
        String nonExistentFile = "nonexistent.txt";

        assertThatThrownBy(() -> ingestor.readData(nonExistentFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to read file: nonexistent.txt");
    }
}
