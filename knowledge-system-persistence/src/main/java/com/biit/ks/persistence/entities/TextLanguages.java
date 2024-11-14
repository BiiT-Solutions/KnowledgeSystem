package com.biit.ks.persistence.entities;

public enum TextLanguages {
    ES,
    EN,
    NL,
    LA;

    public static TextLanguages fromString(String text) {
        for (TextLanguages l : TextLanguages.values()) {
            if (l.toString().equalsIgnoreCase(text)) {
                return l;
            }
        }
        return null;
    }
}
