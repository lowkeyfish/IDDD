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
