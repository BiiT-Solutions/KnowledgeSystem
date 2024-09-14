package com.biit.ks.core.models;

public class Chunk {
    private final byte[] data;
    private final long fileSize;

    public Chunk(byte[] data, long fileSize) {
        this.data = data;
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return data;
    }

    public long getFileSize() {
        return fileSize;
    }
}
