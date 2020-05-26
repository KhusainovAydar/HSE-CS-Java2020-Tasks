package ru.hse.cs.java2020.task02.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.naming.LimitExceededException;

abstract class DiskCache implements Cache {
    private static final String MAPPING_FILE_NAME = "mapping.cache";
    private static final String MAPPING_TMP_FILE_NAME = "mapping.cache.tmp";
    private static final String DUMP_FILE_NAME = "dump.cache";
    private static final String DUMP_TMP_FILE_NAME = "dump.cache.tmp";
    private static final String FILE_OPEN_MODE = "rwd";

    private static final long MAPPING_ENTRY_SIZE = 3;
    private static final long MAPPING_ENTRY_LEN = MAPPING_ENTRY_SIZE * Long.BYTES;

    private static final long DEFAULT_META = 0;
    private static final double COMPACTION_FACTOR = 0.85;

    private final long diskSizeLimit;
    private final String dumpDirectory;

    private final String mappingFilePath;
    private RandomAccessFile mappingFile;
    private final String dumpFilePath;
    private RandomAccessFile dumpFile;

    private long usedDiskSpace = 0;
    private Map<Long, Long> keyToMappingOffset = new HashMap<>();

    DiskCache(long diskSizeLimit, String dumpDirectory) throws IOException {
        this.diskSizeLimit = diskSizeLimit;
        this.dumpDirectory = dumpDirectory;

        Path dumpDirPath = Paths.get(dumpDirectory);
        File mappingFile = dumpDirPath.resolve(MAPPING_FILE_NAME).toFile();
        File dumpFile = dumpDirPath.resolve(DUMP_FILE_NAME).toFile();
        this.mappingFilePath = mappingFile.getAbsolutePath();
        this.mappingFile = new RandomAccessFile(mappingFile, FILE_OPEN_MODE);
        this.dumpFilePath = dumpFile.getAbsolutePath();
        this.dumpFile = new RandomAccessFile(dumpFile, FILE_OPEN_MODE);

        this.usedDiskSpace = mappingFile.length() + dumpFile.length();

        if (usedDiskSpace > 0) {
            load();
        }
    }

    @Override
    public String get(long key) throws IOException {
        byte[] valueRaw = get(key, true);
        return valueRaw == null ? null : new String(valueRaw);
    }

    @Override
    public String put(long key, String value) throws IOException, LimitExceededException {
        if (value == null) {
            throw new NullPointerException("Value can not be null");
        }

        byte[] oldValueRaw = get(key, false);
        byte[] valueRaw = value.getBytes();
        boolean allocateKey = oldValueRaw == null;
        boolean allocateValue = allocateKey || valueRaw.length > oldValueRaw.length;
        changeSize(valueRaw.length, allocateKey, allocateValue);
        allocateKey = !keyToMappingOffset.containsKey(key);
        allocateValue = allocateKey || valueRaw.length > oldValueRaw.length;

        long dumpOffset = dumpFile.length();
        if (!allocateValue) {
            long offset = keyToMappingOffset.get(key) + 2 * Long.BYTES;
            mappingFile.seek(offset);
            dumpOffset = mappingFile.readLong();
        }
        dumpFile.seek(dumpOffset);
        dumpFile.writeInt(valueRaw.length);
        dumpFile.write(valueRaw);

        if (allocateKey) {
            mappingFile.seek(mappingFile.length());
            mappingFile.writeLong(key);
            mappingFile.writeLong(updateMeta(DEFAULT_META));
            mappingFile.writeLong(dumpOffset);
            keyToMappingOffset.put(key, mappingFile.length() - MAPPING_ENTRY_LEN);
        } else {
            long offset = keyToMappingOffset.get(key) + Long.BYTES;
            mappingFile.seek(offset);
            long newMeta = updateMeta(mappingFile.readLong());
            mappingFile.seek(offset);
            mappingFile.writeLong(newMeta);
            mappingFile.writeLong(dumpOffset);
        }

        return oldValueRaw == null ? null : new String(oldValueRaw);
    }

    List<Long> getWarmestKeys() throws IOException {
        Set<KeyMetaEntry> warmestKeys = new TreeSet<>();
        for (Map.Entry<Long, Long> entry : keyToMappingOffset.entrySet()) {
            mappingFile.seek(entry.getValue());
            mappingFile.skipBytes(Long.BYTES);
            warmestKeys.add(new KeyMetaEntry(entry.getKey(), mappingFile.readLong()));
        }
        return warmestKeys.stream().map(KeyMetaEntry::getKey).collect(Collectors.toList());
    }

    KeyValueMeta getFullInfoByKey(long key) throws IOException {
        long mappingOffset = keyToMappingOffset.getOrDefault(key, -1L);
        if (mappingOffset == -1) {
            return null;
        }

        mappingFile.seek(mappingOffset + Long.BYTES);
        long meta = mappingFile.readLong();
        String value = new String(Objects.requireNonNull(get(key, false)));
        return new KeyValueMeta(key, value, meta);
    }

    protected int compareMetas(long lhs, long rhs) {
        return -Long.compare(lhs, rhs);
    }

    protected abstract long updateMeta(long oldMeta);

    private void load() throws IOException {
        long fileSize = mappingFile.length();

        if (fileSize % MAPPING_ENTRY_LEN != 0) {
            throw new IOException(MAPPING_FILE_NAME + " was corrupted.");
        }

        int entriesCount = Math.toIntExact(fileSize / MAPPING_ENTRY_LEN);
        keyToMappingOffset = new HashMap<>(entriesCount);

        for (int i = 0; i != entriesCount; ++i) {
            keyToMappingOffset.put(mappingFile.readLong(), (long) i * MAPPING_ENTRY_LEN);
            int skipMetaAndOffset = 2 * Long.BYTES;
            mappingFile.skipBytes(skipMetaAndOffset);
        }
    }

    private byte[] get(long key, boolean updateMeta) throws IOException {
        long offset = keyToMappingOffset.getOrDefault(key, -1L);
        if (offset == -1) {
            return null;
        }
        offset += Long.BYTES;

        mappingFile.seek(offset);
        if (updateMeta) {
            long meta = mappingFile.readLong();
            meta = updateMeta(meta);
            mappingFile.seek(offset);
            mappingFile.writeLong(meta);
        } else {
            mappingFile.skipBytes(Long.BYTES);
        }

        long dumpOffset = mappingFile.readLong();
        dumpFile.seek(dumpOffset);
        int valueSize = dumpFile.readInt();
        byte[] valueRaw = new byte[valueSize];
        dumpFile.read(valueRaw);
        return valueRaw;
    }

    private void changeSize(int valueRawLen, boolean allocateKey, boolean allocateValue)
            throws IOException, LimitExceededException {
        long entrySize = 0;
        if (allocateKey) {
            entrySize += MAPPING_ENTRY_LEN;
        }
        if (allocateValue) {
            entrySize += valueRawLen;
        }

        if (entrySize > diskSizeLimit) {
            throw new LimitExceededException("Entry size is larger than the provided disk limit.");
        }
        if (entrySize + usedDiskSpace > diskSizeLimit) {
            long limit = (long) Math.max(COMPACTION_FACTOR * diskSizeLimit - entrySize, 0);
            compactify(limit);
        }
        usedDiskSpace += entrySize;
    }

    private void compactify(long limit) throws IOException {
        if (limit == 0) {
            mappingFile.setLength(limit);
            dumpFile.setLength(limit);
            keyToMappingOffset = new HashMap<>();
            return;
        }
        List<Long> warmestKeys = getWarmestKeys();

        File tmpMappingFile = Paths.get(dumpDirectory).resolve(MAPPING_TMP_FILE_NAME).toFile();
        RandomAccessFile tmpMappingRAFile = new RandomAccessFile(tmpMappingFile, FILE_OPEN_MODE);
        File tmpDumpFile = Paths.get(dumpDirectory).resolve(DUMP_TMP_FILE_NAME).toFile();
        RandomAccessFile tmpDumpRAFile = new RandomAccessFile(tmpDumpFile, FILE_OPEN_MODE);

        long newDiskSpace = 0;
        for (int i = 0; i != warmestKeys.size(); ++i) {
            long key = warmestKeys.get(i);
            mappingFile.seek(keyToMappingOffset.get(key) + Long.BYTES);
            long meta = mappingFile.readLong();
            long dumpOffset = mappingFile.readLong();
            dumpFile.seek(dumpOffset);
            int valueSize = dumpFile.readInt();

            if (MAPPING_ENTRY_LEN + valueSize + newDiskSpace >= limit) {
                break;
            }
            newDiskSpace += MAPPING_ENTRY_LEN + valueSize;

            tmpMappingRAFile.writeLong(key);
            tmpMappingRAFile.writeLong(meta);
            tmpMappingRAFile.writeLong(tmpDumpRAFile.length());

            byte[] value = new byte[valueSize];
            dumpFile.read(value);
            tmpDumpRAFile.writeInt(valueSize);
            tmpDumpRAFile.write(value);
        }

        tmpMappingRAFile.close();
        tmpDumpRAFile.close();

        mappingFile.close();
        Files.delete(Paths.get(mappingFilePath));
        dumpFile.close();
        Files.delete(Paths.get(dumpFilePath));

        if (!tmpMappingFile.renameTo(new File(mappingFilePath))) {
            throw new IOException("Can not rename new mapping file");
        }
        mappingFile = new RandomAccessFile(mappingFilePath, FILE_OPEN_MODE);
        if (!tmpDumpFile.renameTo(new File(dumpFilePath))) {
            throw new IOException("Can not rename new dump file");
        }
        dumpFile = new RandomAccessFile(dumpFilePath, FILE_OPEN_MODE);

        usedDiskSpace = mappingFile.length() + dumpFile.length();
        load();
    }

    class KeyMetaEntry implements Comparable<KeyMetaEntry> {
        private final long key;
        private final long meta;

        KeyMetaEntry(long key, long meta) {
            this.key = key;
            this.meta = meta;
        }

        long getKey() {
            return key;
        }

        @Override
        public int compareTo(KeyMetaEntry o) {
            return compareMetas(meta, o.meta);
        }
    }
}
