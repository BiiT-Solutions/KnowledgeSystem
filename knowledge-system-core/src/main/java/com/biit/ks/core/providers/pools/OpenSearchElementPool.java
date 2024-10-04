package com.biit.ks.core.providers.pools;

import com.biit.utils.pool.BasePool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenSearchElementPool<E> extends BasePool<String, E> {

    @Value("${pool.expiration.time:15000}")
    private Long expirationTime;

    @Override
    public long getExpirationTime() {
        return expirationTime;
    }

    @Override
    public boolean isDirty(final E element) {
        return false;
    }
}
