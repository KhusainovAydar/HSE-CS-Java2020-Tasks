package ru.hse.cs.java2020.KhusainovAydar.task01;

public class Directory extends AbstractFileObj {
    private String name;
    private long sizeBytes;
    private int numberOfItems;

    Directory(String dirName, long dirSizeBytes, int dirNumberOfItems) {
        this.name = dirName;
        this.sizeBytes = dirSizeBytes;
        this.numberOfItems = dirNumberOfItems;
    }

    @Override
    public String getName() {
        return name + '/';
    }

    @Override
    public int getNumberOfItems() {
        return numberOfItems;
    }

    @Override
    public long getSizeBytes() {
        return sizeBytes;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
