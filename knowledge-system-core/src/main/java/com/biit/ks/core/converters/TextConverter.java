package com.biit.ks.core.converters;

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


import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.dto.TextLanguagesDTO;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TextConverter extends CategorizedElementConverter<Text, TextDTO, TextConverterRequest> {

    protected TextConverter(CategorizationConverter categorizationConverter) {
        super(categorizationConverter);
    }

    @Override
    protected TextDTO convertElement(TextConverterRequest from) {
        if (from == null) {
            return null;
        }
        final TextDTO textDTO = new TextDTO();
        BeanUtils.copyProperties(from.getEntity(), textDTO);

        final Map<TextLanguagesDTO, String> content = new HashMap<>();
        from.getEntity().getContent().forEach((key, value) -> content.put(TextLanguagesDTO.fromString(key.name()), value));
        textDTO.setContent(content);
        copyCategorizations(from.getEntity(), textDTO);
        return textDTO;
    }

    @Override
    public Text reverse(TextDTO to) {
        if (to == null) {
            return null;
        }
        final Text text = new Text();
        BeanUtils.copyProperties(to, text);
        final Map<TextLanguages, String> content = new HashMap<>();
        to.getContent().forEach((key, value) -> content.put(TextLanguages.fromString(key.name()), value));
        text.setContent(content);
        copyCategorizations(to, text);
        return text;
    }
}
