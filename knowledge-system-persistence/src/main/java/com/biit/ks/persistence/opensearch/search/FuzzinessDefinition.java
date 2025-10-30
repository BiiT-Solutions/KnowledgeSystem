package com.biit.ks.persistence.opensearch.search;

/*-
 * #%L
 * Knowledge System (Persistence)
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

/**
 * Enable fuzziness search.
 */
public class FuzzinessDefinition {
    private final Fuzziness fuzziness;
    private final Integer maxExpansions;
    private final Integer prefixLength;

    public FuzzinessDefinition(Fuzziness fuzziness) {
        this(fuzziness, null, null);
    }

    public FuzzinessDefinition(Fuzziness fuzziness, Integer maxExpansions) {
        this(fuzziness, maxExpansions, null);
    }

    public FuzzinessDefinition(Fuzziness fuzziness, Integer maxExpansions, Integer prefixLength) {
        this.fuzziness = fuzziness;
        this.maxExpansions = maxExpansions;
        this.prefixLength = prefixLength;
    }

    public Fuzziness getFuzziness() {
        return fuzziness;
    }

    public Integer getMaxExpansions() {
        return maxExpansions;
    }

    public Integer getPrefixLength() {
        return prefixLength;
    }
}
