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


import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.ks.persistence.repositories.FormRulesLinkRepository;
import org.springframework.stereotype.Service;

@Service
public class FormRulesLinkProvider {

    private final FormRulesLinkRepository formRulesLinkRepository;

    public FormRulesLinkProvider(FormRulesLinkRepository formRulesLinkRepository) {
        this.formRulesLinkRepository = formRulesLinkRepository;
    }

    private FormRulesLinkRepository getRepository() {
        return formRulesLinkRepository;
    }

    public FormRulesLink save(FormRulesLink formRulesLink) {
        return null;
    }
}
