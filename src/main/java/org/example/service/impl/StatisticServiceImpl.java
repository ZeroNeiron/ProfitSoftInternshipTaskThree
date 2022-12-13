package org.example.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.example.service.StatisticService;
import org.json.simple.JSONValue;

public class StatisticServiceImpl implements StatisticService {
    private static final String TYPE = "<type>";
    private static final String FINE_AMOUNT = "<fine_amount>";
    private int threadsNumber = 1;
    private String inputFilesPath;
    private String outputFilePath;
    private List<Map.Entry<String, Double>> statistic = new ArrayList<>();
    private String delimiter = "</violation>";

    public StatisticServiceImpl() {
    }

    public StatisticServiceImpl(String inputFilesPath,
                                String outputFilePath,
                                int threadsNumber) {
        this.threadsNumber = threadsNumber;
        this.inputFilesPath = inputFilesPath;
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void returnStatistic() {

        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);

        List<Path> allFileNamesInDirectory = getAllFileNamesInDirectory();

        for (Path path : allFileNamesInDirectory) {
            CompletableFuture.supplyAsync(() -> path, executorService)
                    .thenAccept(e -> {
                        statistic = createStatisticList(e);
                        System.out.println(Thread.currentThread().getName() + " " + path);
                    });

        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeToFile();
    }

    private List<Map.Entry<String, Double>> createStatisticList(Path path) {
        try (XmlFileReader reader = new XmlFileReader(path.toString(), delimiter)) {
            String line = reader.readLine();

            while (!line.trim().equals("</violations>")) {
                int typeIndex = line.indexOf(TYPE);
                int amountIndex = line.indexOf(FINE_AMOUNT);
                String type = line.substring(line.indexOf(">", typeIndex) + 1,
                        line.indexOf("<", typeIndex + 1));
                Double value = Double.valueOf(line.substring(
                        line.indexOf(">", amountIndex) + 1, line.indexOf("<", amountIndex + 1)));
                statistic.add(Map.entry(type, value));

                line = reader.readLine();
            }
        }
        return statistic;
    }

    private String getStatistic() {
        Map<String, Double> result = new LinkedHashMap<>();
        statistic.stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)
                )).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return JSONValue.toJSONString(result) + System.lineSeparator();
    }

    private List<Path> getAllFileNamesInDirectory() {
        try {
            return Files.walk(Paths.get(inputFilesPath))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Can`t get files");
        }
    }

    private void writeToFile() {
        String s = getStatistic();
        try (JsonFileWriter jsonWriter = new JsonFileWriter(outputFilePath)) {
            jsonWriter.writeToFile(s);
        } catch (Exception e) {
            throw new RuntimeException("Can`t open file", e);
        }
    }
}
