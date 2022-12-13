package org.example;

import org.example.service.StatisticService;
import org.example.service.impl.StatisticServiceImpl;

public class Main {
    private static final String INPUT_STATISTIC_PATH = "src/main/resources/years";
    private static final String OUTPUT_JSON_PATH = "src/main/resources/outputFiles/statistic.json";
    private static final int THREADS_NUMBER = 8;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        StatisticService statisticService =
                new StatisticServiceImpl(INPUT_STATISTIC_PATH, OUTPUT_JSON_PATH, THREADS_NUMBER);
        statisticService.returnStatistic();

        System.out.println(System.currentTimeMillis() - start);
    }
}
