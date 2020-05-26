package ru.hse.cs.java2020.KhusainovAydar.task01;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public final class DiskAnalyzerUtil {
    private static final int MAX_ELEMENTS = 10;
    private static final double PERCENTS = 100.0;

    private DiskAnalyzerUtil() {
    }

    public static void analyze(String inputDir) throws java.io.IOException {
        inputDir = inputDir.replaceFirst("^~", System.getProperty("user.home"));
        System.out.println(inputDir);

        File dir = new File(inputDir);
        File[] firstLevelFiles = dir.listFiles();

        if (firstLevelFiles == null || firstLevelFiles.length == 0) {
            return;
        }

        final List<RegularFile> topUsagefiles = new ArrayList<>();
        List<AbstractFileObj> firstLevelFilesObj = new ArrayList<>();

        for (File item : firstLevelFiles) {
            final ArrayList<Long> fileSizeList = new ArrayList<>();
            SimpleFileVisitor<Path> fileWalker = new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    if (attr.isSymbolicLink()) {
                        return SKIP_SUBTREE;
                    } else if (attr.isRegularFile()) {
                        fileSizeList.add(file.toFile().length());
                        topUsagefiles.add(new RegularFile(file.toFile().getAbsolutePath(), file.toFile().length()));
                        if (topUsagefiles.size() > MAX_ELEMENTS) {
                            topUsagefiles.sort(AbstractFileObj::compareTo);
                            topUsagefiles.remove(topUsagefiles.size() - 1);
                        }
                    }
                    return CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return CONTINUE;
                }
            };
            if (item.isDirectory()) {
                Files.walkFileTree(Paths.get(item.toURI()), fileWalker);
                long allFileSize = 0;
                for (Long everyFileSize : fileSizeList) {
                    allFileSize = allFileSize + everyFileSize;
                }
                firstLevelFilesObj.add(new Directory(item.getName(), allFileSize, fileSizeList.size()));
            } else if (item.isFile()) {
                firstLevelFilesObj.add(new RegularFile(item.getName(), item.length()));
            }
        }

        firstLevelFilesObj.sort(AbstractFileObj::compareTo);
        long sumFilesSize = 0;
        for (AbstractFileObj item : firstLevelFilesObj) {
            sumFilesSize = sumFilesSize + item.getSize();
        }

        for (int i = 0; i < firstLevelFilesObj.size(); ++i) {
            AbstractFileObj abstractFileObj = firstLevelFilesObj.get(i);
            System.out.println(abstractFileObj.getPrettyPrintString(i + 1,
                    (double) abstractFileObj.getSize() * PERCENTS / (double) sumFilesSize));
        }

        System.out.println("------------------------- BIGGEST FILES -------------------------");
        for (int i = 0; i < topUsagefiles.size(); ++i) {
            RegularFile item = topUsagefiles.get(i);
            System.out.println(item.biggestFilePrettyPrint(i + 1));
        }
    }
}
