package com.biit.ks.persistence.opensearch.search;

/*-
 * #%L
 * Knowledge System (Persistence)
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

public class Range {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private String parameter;
    // gt - lt
    private Object lt;
    private Object lte;
    private Object gt;
    private Object gte;

    public Range() {
    }

    public Range(String parameter, Object gt, Object lt) {
        this();
        this.parameter = parameter;
        this.gt = gt;
        this.lt = lt;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Object getLt() {
        return lt;
    }

    public void setLt(Object lt) {
        this.lt = lt;
    }

    public Object getLte() {
        return lte;
    }

    public void setLte(Object lte) {
        this.lte = lte;
    }

    public Object getGt() {
        return gt;
    }

    public void setGt(Object gt) {
        this.gt = gt;
    }

    public Object getGte() {
        return gte;
    }

    public void setGte(Object gte) {
        this.gte = gte;
    }
}
