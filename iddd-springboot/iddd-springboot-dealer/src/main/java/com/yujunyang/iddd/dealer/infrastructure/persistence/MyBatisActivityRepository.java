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

package com.yujunyang.iddd.dealer.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import com.yujunyang.iddd.common.domain.id.IdGenerator;
import com.yujunyang.iddd.dealer.domain.activity.Activity;
import com.yujunyang.iddd.dealer.domain.activity.ActivityId;
import com.yujunyang.iddd.dealer.domain.activity.ActivityRepository;
import com.yujunyang.iddd.dealer.domain.activity.ActivityStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisActivityRepository implements ActivityRepository {
    private IdGenerator idGenerator;

    @Autowired
    public MyBatisActivityRepository(
            IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public ActivityId nextId() {
        return new ActivityId(idGenerator.nextId());
    }

    @Override
    public Activity findById(ActivityId activityId) {
        return null;
    }

    @Override
    public List<Activity> findBy(String name, List<ActivityStatusType> statusList) {
        return new ArrayList<>();
    }
}
