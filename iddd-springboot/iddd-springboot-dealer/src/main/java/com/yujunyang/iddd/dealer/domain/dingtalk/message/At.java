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

package com.yujunyang.iddd.dealer.domain.dingtalk.message;

import java.util.ArrayList;
import java.util.List;

public class At {
    private List<String> atMobiles;
    private List<String> atUserIds;
    private boolean atAll;

    public At(
            List<String> atMobiles,
            List<String> atUserIds,
            boolean atAll) {
        this.atMobiles = atMobiles;
        this.atUserIds = atUserIds;
        this.atAll = atAll;
    }

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public List<String> getAtUserIds() {
        return atUserIds;
    }

    public boolean isAtAll() {
        return atAll;
    }

    public static At atByMobile(List<String> mobiles) {
        return new At(
                mobiles,
                new ArrayList<>(),
                false
        );
    }

    public static At atByUserId(List<String> userIds) {
        return new At(
                new ArrayList<>(),
                userIds,
                false
        );
    }

    public static At atAll() {
        return new At(
                new ArrayList<>(),
                new ArrayList<>(),
                true
        );
    }

    public static At none() {
        return new At(
                new ArrayList<>(),
                new ArrayList<>(),
                false
        );
    }
}
