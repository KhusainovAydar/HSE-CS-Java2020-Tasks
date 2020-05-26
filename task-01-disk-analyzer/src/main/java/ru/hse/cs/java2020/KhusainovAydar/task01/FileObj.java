package ru.hse.cs.java2020.KhusainovAydar.task01;

public interface FileObj {
    /**
     * Returns a name of file or directory.
     */
    String getName();

    /**
     * Returns a size bytes of file or directory.
     */
    long getSizeBytes();

    /**
     * Returns a size kbytes of file or directory.
     */
    long getSize();

    /**
     * Returns a number of items.
     * Note: 0 if this a regular file
     */
    int getNumberOfItems();

    /**
     * Returns a PrettyPrint String.
     */
    String getPrettyPrintString(int number, double percentOfDiskUsage);

    /**
     * @return True if RegularFile else False.
     */
    boolean isFile();

    /**
     * @return True if Directory else False.
     */
    boolean isDirectory();
}
