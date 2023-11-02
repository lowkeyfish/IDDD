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

package com.yujunyang.iddd.dealer.schedule;

import com.yujunyang.iddd.common.domain.event.notification.NotificationService;
import com.yujunyang.iddd.dealer.domain.dealer.DealerCreated;
import com.yujunyang.iddd.dealer.domain.payment.PaymentFailed;
import com.yujunyang.iddd.dealer.domain.payment.PaymentInitiated;
import com.yujunyang.iddd.dealer.domain.payment.PaymentSucceeded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationScheduler {
    private NotificationService notificationService;

    @Autowired
    public EventNotificationScheduler(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 60 * 1000)
    public void dealerCreated() {
        notificationService.publishNotifications(DealerCreated.class, 50);
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 60 * 1000)
    public void paymentInitiated() {
        notificationService.publishNotifications(PaymentInitiated.class, 50);
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 60 * 1000)
    public void paymentSucceeded() {
        notificationService.publishNotifications(PaymentSucceeded.class, 50);
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 60 * 1000)
    public void paymentFailed() {
        notificationService.publishNotifications(PaymentFailed.class, 50);
    }



}
