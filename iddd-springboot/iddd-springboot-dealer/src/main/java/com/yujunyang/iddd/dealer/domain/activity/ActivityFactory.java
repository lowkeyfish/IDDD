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

package com.yujunyang.iddd.dealer.domain.activity;

import java.util.Map;
import java.util.stream.Collectors;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.common.utils.IdUtils;
import com.yujunyang.iddd.dealer.domain.common.TimeRange;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.gift.Gift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityFactory {
    private ActivityNameUniquenessCheckService activityNameUniquenessCheckService;

    @Autowired
    public ActivityFactory(
            ActivityNameUniquenessCheckService activityNameUniquenessCheckService) {
        this.activityNameUniquenessCheckService = activityNameUniquenessCheckService;
    }

    /*
     * 使用 Dealer 而不是 DealerId，可以避免外部已经有 Dealer 的情况下内部重复去获取，而且可以让内部
     * 不用考虑 Dealer 的获取，减少依赖，保持 Factory 的依赖性和复杂性。还有一点前提是 Activity 并不
     * 直接使用（引用）Dealer，直接引用的不能由外部传入，防止外部保持引用并修改而导致聚合根数据不一致
     */
    public Activity createActivity(
            Dealer dealer,
            String name,
            String summary,
            String image,
            TimeRange visibleTimeRange,
            TimeRange usableTimeRange,
            int participantLimit,
            Map<Gift, Integer> gifts) {
        CheckUtils.notNull(dealer, "dealer 必须不为 null");
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(summary, "summary 必须不为空");
        CheckUtils.notBlank(image, "image 必须不为空");
        CheckUtils.notNull(visibleTimeRange, "visibleTimeRange 必须不为 null");
        CheckUtils.notNull(usableTimeRange, "usableTimeRange 必须不为 null");
        CheckUtils.isTrue(
                !visibleTimeRange.getBegin().isAfter(usableTimeRange.getBegin()),
                "usableTimeRange.begin 必须不早于 visibleTimeRange.begin"
        );
        CheckUtils.isTrue(
                !visibleTimeRange.getEnd().isAfter(usableTimeRange.getEnd()),
                "usableTimeRange.end 必须不早于 visibleTimeRange.end"
        );
        CheckUtils.moreThan(participantLimit, 0, "participantLimit 必须大于 0");

        boolean nameUsed = activityNameUniquenessCheckService.isNameUsed(name);
        CheckUtils.isTrue(!nameUsed, "name 已被其他活动使用");


        return new Activity(
                dealer.getId(),
                new ActivityId(IdUtils.newId()),
                name,
                summary,
                image,
                visibleTimeRange,
                usableTimeRange,
                participantLimit,
                gifts.entrySet().stream().collect(Collectors.toMap(n -> n.getKey().getId(), n -> n.getValue()))
        );
    }
}
