package com.smartqueue.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class FileManager {
    private static final Path DATA_DIR = Path.of("data");

    private static void ensureDataDirectory() throws IOException {
        Files.createDirectories(DATA_DIR);
    }

    public static void append(String fileName, String data) {
        try {
            ensureDataDirectory();
            Files.writeString(DATA_DIR.resolve(fileName), data + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Unable to save data: " + e.getMessage());
        }
    }

    public static List<String> readAll(String fileName) {
        try {
            ensureDataDirectory();
            Path path = DATA_DIR.resolve(fileName);
            if (!Files.exists(path)) return Collections.emptyList();
            return Files.readAllLines(path);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static void overwrite(String fileName, List<String> lines) {
        try {
            ensureDataDirectory();
            Files.write(DATA_DIR.resolve(fileName), lines,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Unable to update data: " + e.getMessage());
        }
    }

    // Kept for compatibility with the original project.
    public static void save(String data) { append("queue_history.txt", data); }
}
