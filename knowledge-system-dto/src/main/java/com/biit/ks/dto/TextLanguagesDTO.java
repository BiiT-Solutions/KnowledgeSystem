package com.biit.ks.dto;

/*-
 * #%L
 * Knowledge System (DTO)
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
