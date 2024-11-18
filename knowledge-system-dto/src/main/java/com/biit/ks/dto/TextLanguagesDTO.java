package com.biit.ks.dto;

public enum TextLanguagesDTO {
    ES,
    EN,
    NL,
    LA;

    public static TextLanguagesDTO fromString(String text) {
        for (TextLanguagesDTO l : TextLanguagesDTO.values()) {
            if (l.toString().equalsIgnoreCase(text)) {
                return l;
            }
        }
        return null;
    }
}
