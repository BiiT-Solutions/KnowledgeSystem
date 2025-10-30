package com.biit.ks.core.providers.pools;

/*-
 * #%L
 * Knowledge System (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.ks.persistence.entities.FileEntry;
import com.biit.utils.pool.BasePool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.biit.ks.core.providers.pools.BasePool.DEFAULT_EXPIRATION_TIME;

@Component
public class FileEntryByStringPool extends BasePool<String, FileEntry> {

    private final long expirationTime;

    public FileEntryByStringPool(@Value("${pool.expiration.time:15000}") final String expirationTime) {
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
