package com.biit.ks;

/*-
 * #%L
 * Knowledge System (Rest Client)
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

import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class OpenSearchTestConfigurator implements IOpenSearchConfigurator {

    public static final String OPENSEARCH_FILE_INDEX = "file-index-test";

    public static final String OPENSEARCH_TEXT_INDEX = "text-index-test";

    public static final String OPENSEARCH_CATEGORIZATIONS_INDEX = "categorizations-test";

    @Override
    public String getOpenSearchFileIndex() {
        return OPENSEARCH_FILE_INDEX;
    }

    @Override
    public String getOpenSearchTextIndex() {
        return OPENSEARCH_TEXT_INDEX;
    }

    @Override
    public String getOpenSearchCategorizationsIndex() {
        return OPENSEARCH_CATEGORIZATIONS_INDEX;
    }

    @Override
    public List<String> getAllOpenSearchIndexes() {
        return List.of(getOpenSearchFileIndex(), getOpenSearchTextIndex(), getOpenSearchCategorizationsIndex());
    }
}
