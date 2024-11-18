package com.biit.ks.dto;

public enum TextLanguagesDTO {
    ES,
    EN,
    NL,
    LA;

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
        }
        return TextLanguagesDTO.EN;
    }
}
