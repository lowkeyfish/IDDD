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
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.gift.Gift;
import com.yujunyang.iddd.dealer.domain.gift.GiftId;
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

    public Activity create(
            DealerId dealerId,
            String name,
            String summary,
            String image,
            TimeRange registrationTimeRange,
            TimeRange participationTimeRange,
            int participantLimit,
            Map<GiftId, Integer> gifts) {
        CheckUtils.notNull(dealerId, "dealerId 必须不为 null");
        CheckUtils.notBlank(name, "name 必须不为空");
        CheckUtils.notBlank(summary, "summary 必须不为空");
        CheckUtils.notBlank(image, "image 必须不为空");
        CheckUtils.notNull(registrationTimeRange, "registrationTimeRange 必须不为 null");
        CheckUtils.notNull(participationTimeRange, "participationTimeRange 必须不为 null");
        CheckUtils.moreThan(participantLimit, 0, "participantLimit 必须大于 0");

        boolean nameUsed = activityNameUniquenessCheckService.isNameUsed(name);
        CheckUtils.isTrue(!nameUsed, "name 已被其他活动使用");

        return new Activity(
                new ActivityId(IdUtils.newId()),
                dealerId,
                name,
                summary,
                image,
                registrationTimeRange,
                participationTimeRange,
                participantLimit,
                gifts
        );
    }
}


