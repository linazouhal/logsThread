package Presentation.layer;
import java.time.LocalDateTime;

public class LogEntry {

    private LocalDateTime timestamp;
    private String level, logger, message;

    public LogEntry(LocalDateTime timestamp, String level,
                    String logger, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.logger = logger;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getLogger() {
        return logger;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", logger='" + logger + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

