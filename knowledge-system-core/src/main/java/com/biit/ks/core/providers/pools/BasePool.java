package com.biit.ks.core.providers.pools;

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


import com.biit.logger.BiitPoolLogger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePool<ElementId, Type> {
    protected static final long DEFAULT_EXPIRATION_TIME = 15 * 1000; //15 seconds

    // Elements by id;
    private Map<ElementId, Long> elementsTime; // user id -> time.
    private Map<ElementId, Type> elementsById;

    public BasePool() {
        reset();
    }

    public synchronized void reset() {
        BiitPoolLogger.debug(this.getClass(), "Reseting all pool.");
        elementsTime = new ConcurrentHashMap<ElementId, Long>();
        elementsById = new ConcurrentHashMap<ElementId, Type>();
    }

    public synchronized void addElement(final Type element, final ElementId key) {
        BiitPoolLogger.debug(this.getClass(), "Adding element '" + element + "' with key '" + key + "'.");
        if (getExpirationTime() > 0) {
            elementsTime.put(key, System.currentTimeMillis());
            elementsById.put(key, element);
        }
    }

    /**
     * Gets all previously stored elements of a user in a site.
     *
     * @param elementId element key for the pool.
     * @return the element that has the selected key.
     */
    public synchronized Type getElement(final ElementId elementId) {
        if (elementId != null && getExpirationTime() > 0) {
            final long now = System.currentTimeMillis();
            ElementId storedObjectId = null;
            if (elementsTime.size() > 0) {
                BiitPoolLogger.debug(this.getClass(), "Elements on cache: " + elementsTime.size() + ".");
                final Map<ElementId, Long> elementsByTimeChecked = new ConcurrentHashMap<>(elementsTime);
                final Map<ElementId, Type> elementsByIdChecked = new ConcurrentHashMap<>(elementsById);
                final Iterator<ElementId> elementByTime = elementsByTimeChecked.keySet().iterator();

                for (final Entry<ElementId, Long> elementsByTimeEntry : elementsByTimeChecked.entrySet()) {
                    storedObjectId = elementByTime.next();
                    if (elementsByTimeEntry.getValue() != null
                            && (now - elementsByTimeEntry.getValue()) > getExpirationTime()) {
                        BiitPoolLogger.debug(this.getClass(), "Element '" + elementsByTimeEntry.getValue()
                                + "' has expired (elapsed time: '" + (now - elementsByTimeEntry.getValue()) + "' > '"
                                + getExpirationTime() + "'.)");
                        // object has expired
                        removeElement(storedObjectId);
                        storedObjectId = null;
                    } else {
                        if (elementsByIdChecked.get(storedObjectId) != null) {
                            // Remove not valid elements.
                            if (isDirty(elementsByIdChecked.get(storedObjectId))) {
                                BiitPoolLogger.debug(this.getClass(),
                                        "Cache: " + elementsByIdChecked.get(storedObjectId).getClass().getName()
                                                + " is dirty! ");
                                removeElement(storedObjectId);
                            } else if (Objects.equals(storedObjectId, elementId)) {
                                BiitPoolLogger.info(this.getClass(), "Cache: "
                                        + elementsByIdChecked.get(storedObjectId).getClass().getName()
                                        + " store hit for " + elementId);
                                return elementsByIdChecked.get(storedObjectId);
                            }
                        }
                    }
                }
            }
        }
        BiitPoolLogger.debug(this.getClass(), "Object with Id '" + elementId + "' - Cache Miss.");
        return null;
    }

    protected synchronized void cleanExpired() {
        final long now = System.currentTimeMillis();
        for (final ElementId elementId : new ConcurrentHashMap<>(elementsTime).keySet()) {
            final ElementId storedObjectId = elementId;
            if (elementsTime.get(storedObjectId) != null
                    && (now - elementsTime.get(storedObjectId)) > getExpirationTime()) {
                BiitPoolLogger.debug(this.getClass(), "Element '" + elementsTime.get(storedObjectId)
                        + "' has expired (elapsed time: '" + (now - elementsTime.get(storedObjectId)) + "' > '"
                        + getExpirationTime() + "'.)");
                // object has expired
                removeElement(storedObjectId);
            } else {
                if (elementsById.get(storedObjectId) != null) {
                    // Remove not valid elements.
                    if (isDirty(elementsById.get(storedObjectId))) {
                        BiitPoolLogger.debug(this.getClass(), "Cache: "
                                + elementsById.get(storedObjectId).getClass().getName() + " is dirty! ");
                        removeElement(storedObjectId);
                    }
                }
            }
        }
    }

    public synchronized ElementId getKey(final Type element) {
        if (element != null && getExpirationTime() > 0) {
            final long now = System.currentTimeMillis();
            ElementId storedObjectId = null;
            if (!elementsTime.isEmpty()) {
                BiitPoolLogger.debug(this.getClass(), "Elements on cache: " + elementsTime.size() + ".");
                for (final ElementId elementId : new ConcurrentHashMap<ElementId, Long>(elementsTime).keySet()) {
                    storedObjectId = elementId;
                    if (elementsTime.get(storedObjectId) != null
                            && (now - elementsTime.get(storedObjectId)) > getExpirationTime()) {
                        BiitPoolLogger.debug(this.getClass(), "Element '" + elementsTime.get(storedObjectId)
                                + "' has expired (elapsed time: '" + (now - elementsTime.get(storedObjectId)) + "' > '"
                                + getExpirationTime() + "'.)");
                        // object has expired
                        removeElement(storedObjectId);
                        storedObjectId = null;
                    } else {
                        if (elementsById.get(storedObjectId) != null) {
                            // Remove not valid elements.
                            if (isDirty(elementsById.get(storedObjectId))) {
                                BiitPoolLogger.debug(this.getClass(), "Cache: "
                                        + elementsById.get(storedObjectId).getClass().getName() + " is dirty! ");
                                removeElement(storedObjectId);
                            } else if (Objects.equals(elementsById.get(storedObjectId), element)) {
                                BiitPoolLogger.info(this.getClass(), "Cache: "
                                        + elementsById.get(storedObjectId).getClass().getName() + " store hit for "
                                        + element);
                                return storedObjectId;
                            }
                        }
                    }
                }
            }
        }
        BiitPoolLogger.debug(this.getClass(), "Object '" + element + "' - Cache Miss.");
        return null;
    }

    public abstract long getExpirationTime();

    public Set<Type> getAllPooledElements() {
        return new HashSet<>(elementsById.values());
    }

    public Set<ElementId> getAllPooledKeys() {
        return new HashSet<>(elementsById.keySet());
    }

    public Map<ElementId, Type> getElementsById() {
        return elementsById;
    }

    public Map<ElementId, Long> getElementsTime() {
        return elementsTime;
    }

    public synchronized Type removeElement(final ElementId elementId) {
        if (elementId != null) {
            BiitPoolLogger.debug(this.getClass(), "Removing element '" + elementId + "'.");
            elementsTime.remove(elementId);
            return elementsById.remove(elementId);
        }
        return null;
    }

    /**
     * An element is dirty if cannot be used by the pool any more.
     *
     * @param element element to check
     * @return if it is dirty or not.
     */
    public abstract boolean isDirty(Type element);

    public int size() {
        return elementsById.size();
    }
}
