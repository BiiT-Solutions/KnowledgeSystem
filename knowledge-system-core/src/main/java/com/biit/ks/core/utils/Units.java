package com.biit.ks.core.utils;

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

public final class Units {
  private Units() { }

  public static final long KILOBYTE = 1024;
  public static final long MEGABYTE = 1024 * KILOBYTE;
  public static final long GIGABYTE = 1024 * MEGABYTE;
  public static final long TERABYTE = 1024 * GIGABYTE;

  public static final long KB = KILOBYTE;
  public static final long MB = MEGABYTE;
  public static final long GB = GIGABYTE;
  public static final long TB = TERABYTE;
}
