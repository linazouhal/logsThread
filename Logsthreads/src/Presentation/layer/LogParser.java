package Presentation.layer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogParser {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static LogEntry parseLogLine(String line) {

        try {

            // 1. Verifier la longueur minimale
            if (line == null || line.length() < 50) {
                return null;
            }

            // 2. Extraire le timestamp
            String timestampStr = line.substring(0, 23);

            LocalDateTime timestamp =
                    LocalDateTime.parse(timestampStr, DTF);

            // 3. Extraire le niveau de log
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

            // 4. Extraire le logger
            int loggerEnd = line.indexOf(" - ", end);

            if (loggerEnd == -1) {
                return null;
            }

            String logger =
                    line.substring(end + 1, loggerEnd).trim();

            // 5. Extraire le message
            String message =
                    line.substring(loggerEnd + 3).trim();

            // 6. Creer et retourner LogEntry
            return new LogEntry(timestamp, level, logger, message);

        } catch (Exception e) {
            return null;
        }
    }
}
