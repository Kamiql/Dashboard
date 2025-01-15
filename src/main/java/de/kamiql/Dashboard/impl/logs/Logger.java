package de.kamiql.Dashboard.impl.logs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("logger")
public class Logger {
    private static final String LOG_FILE_PATH = "logs/log.txt";
    private static final long MAX_LOG_SIZE = 200L * 1024L;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Logger() {
        ensureLogDirectoryExists();
        ensureLogFileExists();
    }

    public void log(LogLevel level, String loggerName, String message) {
        String formattedMessage = formatMessage(level, loggerName, message);
        executor.submit(() -> writeLogToFile(formattedMessage));
    }

    public void fatal(String loggerName, String message) {
        log(LogLevel.FATAL, loggerName, message);
    }

    public void error(String loggerName, String message) {
        log(LogLevel.ERROR, loggerName, message);
    }

    public void warn(String loggerName, String message) {
        log(LogLevel.WARN, loggerName, message);
    }

    public void info(String loggerName, String message) {
        log(LogLevel.INFO, loggerName, message);
    }

    private String formatMessage(LogLevel level, String loggerName, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        String style = getLogLevelColor(level);
        return String.format("%s %s | [%s] [%s] %s", style, timestamp, level.name(), loggerName, message);
    }

    private String getLogLevelColor(LogLevel level) {
        return switch (level) {
            case FATAL -> "fatal-log";
            case ERROR -> "error-log";
            case WARN -> "warn-log";
            default -> "info-log";
        };
    }

    private void writeLogToFile(String formattedMessage) {
        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            if (Files.exists(logPath) && Files.size(logPath) >= MAX_LOG_SIZE) {
                rotateLogs(logPath);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(logPath.toFile(), true));
            writer.write(formattedMessage);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rotateLogs(Path logPath) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path oldLogPath = Paths.get("logs", timestamp + "__log.txt.old");

        Files.move(logPath, oldLogPath, StandardCopyOption.REPLACE_EXISTING);

        Files.createFile(logPath);
    }

    public List<String> readLogs() {
        List<String> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    private void ensureLogDirectoryExists() {
        Path logDir = Paths.get("logs");
        if (!Files.exists(logDir)) {
            try {
                Files.createDirectories(logDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ensureLogFileExists() {
        Path logFile = Paths.get(LOG_FILE_PATH);
        if (!Files.exists(logFile)) {
            try {
                Files.createFile(logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum LogLevel {
        FATAL,
        ERROR,
        WARN,
        INFO
    }
}
