/*
 * Copyright 2023 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
