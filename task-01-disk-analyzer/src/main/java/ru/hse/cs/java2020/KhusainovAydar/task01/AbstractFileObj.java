package ru.hse.cs.java2020.KhusainovAydar.task01;

abstract class AbstractFileObj implements FileObj, Comparable<AbstractFileObj> {
    private static final int BYTES_IN_KBYTE = 1024;

    @Override
    public final int compareTo(AbstractFileObj other) {
        return -Long.compare(this.getSize(), other.getSize());
    }

    @Override
    public final String getPrettyPrintString(int number, double percentOfDiskUsage) {
        if (this.isDirectory()) {
            return String.format("%3d.%40s|%8d Kb|%5.2f%%|%7d items",
                    number, this.getName(), this.getSize(), percentOfDiskUsage, this.getNumberOfItems());
        } else {
            return String.format("%3d.%40s|%8d Kb|%5.2f%%",
                    number, this.getName(), this.getSize(), percentOfDiskUsage);
        }
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long getSize() {
        return this.getSizeBytes() / BYTES_IN_KBYTE;
    }
}
