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
 *
 */

package com.yujunyang.iddd.dealer.common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.yujunyang.iddd.common.utils.CheckUtils;

public class TimeRange {
    private LocalDateTime begin;
    private LocalDateTime end;

    public TimeRange(
            LocalDateTime begin,
            LocalDateTime end) {
        CheckUtils.notNull(begin, "begin 必须不为 null");
        CheckUtils.notNull(end, "end 必须不为 null");
        CheckUtils.isTrue(
                begin.isEqual(end) ||
                begin.isBefore(end), "begin 必须不晚于 end");

        this.begin = begin;
        this.end = end;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public boolean include(LocalDateTime time) {
        CheckUtils.notNull(time, "time 必须不为 null");

        return (begin.isEqual(time) || begin.isBefore(time)) && (end.isEqual(time) || end.isAfter(time));
    }

    public boolean notStarted() {
        LocalDateTime now = LocalDateTime.now();
        return begin.isAfter(now);
    }

    public boolean inProgress() {
        LocalDateTime now = LocalDateTime.now();
        return include(now);
    }

    public boolean ended() {
        LocalDateTime now = LocalDateTime.now();
        return end.isBefore(now);
    }

    public TimeRange advanceBegin(long amount, ChronoUnit unit) {
        CheckUtils.moreThan(amount, 0, "amount 必须大于 0");
        CheckUtils.notNull(unit, "unit 必须不为 null");

        return updateBegin(begin.minus(amount, unit));
    }

    public TimeRange delayBegin(long amount, ChronoUnit unit) {
        CheckUtils.moreThan(amount, 0, "amount 必须大于 0");
        CheckUtils.notNull(unit, "unit 必须不为 null");

        return updateBegin(begin.plus(amount, unit));
    }

    public TimeRange updateBegin(LocalDateTime newBegin) {
        CheckUtils.isTrue(newBegin.isBefore(end), "begin 必须早于 end");

        return new TimeRange(
                newBegin,
                end
        );
    }

    public TimeRange delayEnd(long amount, ChronoUnit unit) {
        CheckUtils.moreThan(amount, 0, "amount 必须大于 0");
        CheckUtils.notNull(unit, "unit 必须不为 null");

        return updateEnd(end.plus(amount, unit));
    }

    public TimeRange advanceEnd(long amount, ChronoUnit unit) {
        CheckUtils.moreThan(amount, 0, "amount 必须大于 0");
        CheckUtils.notNull(unit, "unit 必须不为 null");

        return updateEnd(end.minus(amount, unit));
    }

    public TimeRange updateEnd(LocalDateTime newEnd) {
        CheckUtils.isTrue(newEnd.isAfter(begin), "end 必须晚于 begin");

        return new TimeRange(
                begin,
                newEnd
        );
    }

}
