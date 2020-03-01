package ru.hse.cs.java2020.KhusainovAydar.task01;

import java.io.IOException;


public final class Main {
    private Main() {
    }

    private static final double TO_SECONDS = 1000F;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        if (args.length == 0) {
            return;
        }
        String inputDir = args[0];

        DiskAnalyzerUtil.analyze(inputDir);

        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) / TO_SECONDS + " s");
    }
}
