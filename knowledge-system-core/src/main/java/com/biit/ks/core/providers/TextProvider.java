package com.biit.ks.core.providers;

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

import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.TextRepository;
import org.springframework.stereotype.Service;

@Service
public class TextProvider extends CategorizedElementProvider<Text, TextRepository> {


    protected TextProvider(TextRepository textRepository) {
        super(textRepository);
    }


    public SearchWrapper<Text> search(String searchQuery, TextLanguages language, Integer from, Integer size) {
        return getRepository().search(searchQuery, language, from, size);
    }


    //Name is unique on texts
    public SearchWrapper<Text> get(String name) {
        final SearchWrapper<Text> texts = getRepository().get(name);
        if (texts.isEmpty()) {
            return new SearchWrapper<>();
        }
        return texts;
    }

}
