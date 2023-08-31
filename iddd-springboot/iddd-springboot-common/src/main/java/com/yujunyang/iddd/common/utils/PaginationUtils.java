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
