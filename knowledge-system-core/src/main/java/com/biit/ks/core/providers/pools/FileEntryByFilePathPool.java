package com.biit.ks.core.providers.pools;

import com.biit.ks.persistence.entities.FileEntry;
import com.biit.utils.pool.BasePool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.biit.ks.core.providers.pools.BasePool.DEFAULT_EXPIRATION_TIME;

@Component
public class FileEntryByFilePathPool extends BasePool<String, FileEntry> {

  private final long expirationTime;

  public FileEntryByFilePathPool(@Value("${action-book.pool.expiration.time:600000}") final String expirationTime) {
    long calculatedExpirationTime;
    try {
      calculatedExpirationTime = Long.parseLong(expirationTime);
    } catch (final NumberFormatException e) {
      calculatedExpirationTime = DEFAULT_EXPIRATION_TIME;
    }
    this.expirationTime = calculatedExpirationTime;
  }

  @Override
  public long getExpirationTime() {
    return expirationTime;
  }

  @Override
  public boolean isDirty(final FileEntry element) {
    return false;
  }
}
