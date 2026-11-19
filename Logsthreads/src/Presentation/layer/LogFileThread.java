package Presentation.layer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogFileThread implements Runnable {

    private Path pathFile;
    private List<LogEntry> sharedList;

    public LogFileThread(Path pathFile,
                         List<LogEntry> logEntries) {
        this.pathFile = pathFile;
        this.sharedList = logEntries;
    }

    @Override
    public void run() {

        try (Stream<String> lines = Files.lines(pathFile)) {

            List<LogEntry> local = lines
                    .map(LogParser::parseLogLine)
                    .filter(Objects::nonNull)
                    .toList();

            sharedList.addAll(local);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }}
