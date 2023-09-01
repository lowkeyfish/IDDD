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

package com.yujunyang.iddd.common.data;

import java.util.List;

import com.yujunyang.iddd.common.utils.PaginationUtils;

public class Pagination<T> {
    private int totalCount;
    private int pageSize;
    private int pageIndex;
    private List<T> items;

    public Pagination(
            int totalCount,
            int pageSize,
            int pageIndex,
            List<T> items) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageCount() {
        return PaginationUtils.pageCount(this.totalCount, this.pageSize);
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public List<T> getItems() {
        return items;
    }
}
