/*
 * Copyright 2023 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * This file is part of IDDD.
 *
 * IDDD is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IDDD is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IDDD.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.yujunyang.iddd.common.utils;

public final class PaginationUtils {
    public static int offset(int pageIndex, int pageSize) {
        return (safePageIndex(pageIndex) - 1) * safePageSize(pageSize);
    }

    public static int pageCount(int totalCount, int pageSize) {
        pageSize = safePageSize(pageSize);
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    public static int safePageIndex(int pageIndex) {
        if (pageIndex <= 1) {
            return 1;
        }
        return pageIndex;
    }

    public static int safePageSize(int pageSize) {
        if (pageSize < 1) {
            return 10;
        }
        return pageSize;
    }
}
