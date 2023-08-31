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
