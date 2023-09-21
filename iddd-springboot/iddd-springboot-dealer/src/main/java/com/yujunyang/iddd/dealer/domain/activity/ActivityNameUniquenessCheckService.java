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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityNameUniquenessCheckService {
    private ActivityRepository activityRepository;

    @Autowired
    public ActivityNameUniquenessCheckService(
            ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public boolean isNameUsed(String name) {
        List<Activity> activityList =
                activityRepository.findBy(name, Arrays.asList(ActivityStatusType.PENDING, ActivityStatusType.STARTED));
        return !CollectionUtils.isEmpty(activityList);
    }

    public boolean isNameUsed(Activity self, String name) {
        List<Activity> activityList = activityRepository.findBy(
                name,
                Arrays.asList(ActivityStatusType.PENDING, ActivityStatusType.STARTED))
                .stream()
                .filter(n -> !n.id().equals(self.id())).collect(Collectors.toList());
        return !CollectionUtils.isEmpty(activityList);
    }
}
