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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityRegistrationLimitService {
    private ActivityRegistrationRepository activityRegistrationRepository;

    @Autowired
    public ActivityRegistrationLimitService(
            ActivityRegistrationRepository activityRegistrationRepository) {
        this.activityRegistrationRepository = activityRegistrationRepository;
    }

    public boolean isRestricted(Activity activity, Participant participant) {
        ActivityRegistration activityRegistration = activityRegistrationRepository.findBy(
                activity.getId(), participant.getMobileNumber());
        return activityRegistration != null;
    }
}