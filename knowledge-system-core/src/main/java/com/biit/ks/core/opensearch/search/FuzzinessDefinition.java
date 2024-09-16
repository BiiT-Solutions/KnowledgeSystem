package com.biit.ks.core.opensearch.search;

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

    FuzzinessDefinition(Fuzziness fuzziness, Integer maxExpansions, Integer prefixLength) {
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
