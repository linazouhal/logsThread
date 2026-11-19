package Presentation.layer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogParser {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static LogEntry parseLogLine(String line) {

        try {

            if (line == null || line.length() < 50) {
                return null;
            }

            String timestampStr = line.substring(0, 23);

            LocalDateTime timestamp =
                    LocalDateTime.parse(timestampStr, DTF);

            int start = line.indexOf("[", 24);

            if (start == -1) {
                return null;
            }

            int end = line.indexOf("]", start);

            if (end == -1) {
                return null;
            }

            String level =
                    line.substring(start + 1, end).trim();

            int loggerEnd = line.indexOf(" - ", end);

            if (loggerEnd == -1) {
                return null;
            }

            String logger =
                    line.substring(end + 1, loggerEnd).trim();

            String message =
                    line.substring(loggerEnd + 3).trim();

            return new LogEntry(timestamp, level, logger, message);

        } catch (Exception e) {
            return null;
        }
    }
}
