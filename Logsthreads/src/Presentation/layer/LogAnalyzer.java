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

        // Recuperer les fichiers logs
        Path directory = Path.of("C:\\Users\\user\\IdeaProjects\\Logsthreads\\src\\logs");

        List<Path> files;

        try (Stream<Path> paths = Files.list(directory)) {
            files = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        // Niveau 1 : Threads simples
        System.out.println("Traitement avec Threads simples...");

        List<LogEntry> entries1 = processWithThreads(files);

        displayStatistics(entries1);

        System.out.println("\n================================");

        // Niveau 2 : ExecutorService
        System.out.println("Traitement avec ExecutorService...");

        List<LogEntry> entries2 = processWithExecutor(files);

        displayStatistics(entries2);
    }

    // Methode 1 : Threads simples
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

        // Attendre la fin de tous les threads
        for (Thread thread : threads) {
            thread.join();
        }

        return allEntries;
    }

    // Methode 2 : ExecutorService
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

    // Methode pour afficher les statistiques
    public static void displayStatistics(List<LogEntry> entries) {

        // Nombre total de logs
        System.out.println(
                "Nombre total d'entrees : " + entries.size()
        );

        // Logs par niveau
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

        // Logger le plus actif
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

        // Nombre de logs ERROR
        long errorCount = entries.stream()
                .filter(e -> e.getLevel().equals("ERROR"))
                .count();

        System.out.println(
                "Nombre de logs ERROR : " + errorCount
        );
    }
}