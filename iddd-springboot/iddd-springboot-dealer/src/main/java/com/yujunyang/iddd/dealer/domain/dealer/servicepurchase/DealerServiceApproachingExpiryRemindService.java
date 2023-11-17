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

package com.yujunyang.iddd.dealer.domain.dealer.servicepurchase;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerServiceApproachingExpiryRemindService {
    private SmsService smsService;

    @Autowired
    public DealerServiceApproachingExpiryRemindService(
            SmsService smsService) {
        this.smsService = smsService;
    }

    public void remind(Dealer dealer) {
        CheckUtils.notNull(dealer, "dealer 必须不为 null");

        if (!dealer.isInService()) {
            return;
        }

        LocalDateTime serviceExpiryTimeNeedRemind = LocalDate.now().minusDays(7).atStartOfDay();
        if (serviceExpiryTimeNeedRemind.isBefore(dealer.serviceExpiryTime())) {
            smsService.sendSms(
                    "18812341234",
                    "服务即将到期",
                    "DealerServiceApproachingExpiryRemind",
                    dealer.id().toString()
            );
        }
    }
}
