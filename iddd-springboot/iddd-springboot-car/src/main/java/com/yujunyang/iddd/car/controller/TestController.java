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

package com.yujunyang.iddd.car.controller;

import javax.annotation.Resource;

import com.yujunyang.iddd.common.domain.event.DomainEvent;
import com.yujunyang.iddd.common.domain.event.notification.PublishedNotificationTrackerStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private PublishedNotificationTrackerStore publishedNotificationTrackerStore;

    @GetMapping("")
    public Object test() {
        return publishedNotificationTrackerStore.publishedNotificationTracker(Test.class);
    }

    public static class Test implements DomainEvent {

        @Override
        public long getTimestamp() {
            return 0;
        }

        @Override
        public String eventKey() {
            return null;
        }

        @Override
        public String storedEventType() {
            return null;
        }

        @Override
        public String notificationType() {
            return null;
        }

        @Override
        public String notificationExchange() {
            return null;
        }

        @Override
        public String notificationRoutingKey() {
            return null;
        }
    }
}
