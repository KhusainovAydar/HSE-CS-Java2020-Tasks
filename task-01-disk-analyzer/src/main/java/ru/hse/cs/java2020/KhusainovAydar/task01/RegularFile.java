package ru.hse.cs.java2020.KhusainovAydar.task01;

public class RegularFile extends AbstractFileObj {
    private final String name;
    private final long sizeBytes;
    RegularFile(String fileName, long fileSizeBytes) {
        this.name = fileName;
        this.sizeBytes = fileSizeBytes;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public int getNumberOfItems() {
        return 0;
    }
    @Override
    public long getSizeBytes() {
        return sizeBytes;
    }
    @Override
    public boolean isFile() {
        return true;
    }

    String biggestFilePrettyPrint(int number) {
        return String.format("%3d.%8d Kb| %s", number, this.getSize(), this.getName());
    }
}
