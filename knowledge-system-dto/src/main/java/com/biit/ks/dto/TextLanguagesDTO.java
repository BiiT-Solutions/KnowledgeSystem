package com.biit.ks.dto;

import java.util.Locale;

public enum TextLanguagesDTO {
    ES(Locale.forLanguageTag("es-ES")),
    EN(Locale.ENGLISH),
    NL(Locale.forLanguageTag("nl-NL")),
    LA(Locale.forLanguageTag("LA"));

    private final Locale locale;

    TextLanguagesDTO(Locale locale) {
        this.locale = locale;
    }

    public static TextLanguagesDTO fromString(String text) {
        for (TextLanguagesDTO textLanguagesDTO : TextLanguagesDTO.values()) {
            if (textLanguagesDTO.toString().equalsIgnoreCase(text)) {
                return textLanguagesDTO;
            }
            //For locales
            if (text.contains("_") && textLanguagesDTO.toString().equalsIgnoreCase(text.split("_")[0])) {
                return textLanguagesDTO;
            }
            if (text.contains("-") && textLanguagesDTO.toString().equalsIgnoreCase(text.split("-")[0])) {
                return textLanguagesDTO;
            }
            if (Locale.forLanguageTag(text).equals(textLanguagesDTO.locale)) {
                return textLanguagesDTO;
            }
        }
        return TextLanguagesDTO.EN;
    }
}
