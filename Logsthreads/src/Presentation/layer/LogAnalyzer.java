package Presentation.layer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogAnalyzer {

    public static void main(String[] args)
            throws IOException, InterruptedException {

        Path directory = Path.of("C:\\Users\\user\\IdeaProjects\\Logsthreads\\src\\logs");

        List<Path> files;

        try (Stream<Path> paths = Files.list(directory)) {
            files = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        System.out.println("Traitement avec Threads simples...");

        List<LogEntry> entries1 = processWithThreads(files);

        displayStatistics(entries1);

        System.out.println("\n================================");

        System.out.println("Traitement avec ExecutorService...");

        List<LogEntry> entries2 = processWithExecutor(files);

        displayStatistics(entries2);
    }

    public static List<LogEntry> processWithThreads(List<Path> files)
            throws InterruptedException {

        List<Thread> threads = new ArrayList<>();

        List<LogEntry> allEntries =
                new CopyOnWriteArrayList<>();

        for (Path file : files) {

            LogFileThread task =
                    new LogFileThread(file, allEntries);

            Thread t = new Thread(task);

            t.start();

            threads.add(t);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return allEntries;
    }

    public static List<LogEntry> processWithExecutor(List<Path> files)
            throws InterruptedException {

        List<LogEntry> allEntries =
                new CopyOnWriteArrayList<>();

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors()
                );

        for (Path file : files) {

            executor.submit(
                    new LogFileThread(file, allEntries)
            );
        }

        executor.shutdown();

        executor.awaitTermination(1, TimeUnit.HOURS);

        return allEntries;
    }
    public static void displayStatistics(List<LogEntry> entries) {

        System.out.println(
                "Nombre total d'entrees : " + entries.size()
        );

        Map<String, Long> logsByLevel = entries.stream()
                .collect(Collectors.groupingBy(
                        LogEntry::getLevel,
                        Collectors.counting()
                ));

        logsByLevel.forEach((level, count) ->
                System.out.println(
                        "Niveau " + level + " : " + count
                )
        );

        entries.stream()
                .collect(Collectors.groupingBy(
                        LogEntry::getLogger,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry ->
                        System.out.println(
                                "Logger le plus actif : "
                                        + entry.getKey()
                                        + " avec "
                                        + entry.getValue()
                                        + " logs"
                        )
                );

        long errorCount = entries.stream()
                .filter(e -> e.getLevel().equals("ERROR"))
                .count();

        System.out.println(
                "Nombre de logs ERROR : " + errorCount
        );
    }
}
