package com.biit.ks.core.models;

public class ChunkData  extends Chunk {

  private final String mimeType;

  public ChunkData(final Chunk chunk, final String mimeType) {
    super(chunk.getData(), chunk.getFileSize());
    this.mimeType = mimeType;
  }

  public ChunkData(final byte[] data, final long fileSize, final String mimeType) {
    super(data, fileSize);
    this.mimeType = mimeType;
  }

  public String getMimeType() {
    return mimeType;
  }
}
