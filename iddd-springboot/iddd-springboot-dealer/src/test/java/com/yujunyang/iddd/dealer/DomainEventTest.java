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

package com.yujunyang.iddd.dealer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yujunyang.iddd.common.utils.JacksonUtils;
import com.yujunyang.iddd.dealer.domain.dealer.DealerCreated;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class DomainEventTest {
    @Test
    public void test_extend_domain_event_serialize_deserialize() throws JSONException {
        DealerCreated domainEvent = new DealerCreated(
                1,
                1
        );

        assertEquals("DealerId(1)", domainEvent.eventKey());
        assertEquals("Dealer.DealerCreated", domainEvent.notificationRoutingKey());
        assertEquals("DealerCreated", domainEvent.notificationType());

        String json = JacksonUtils.serialize(domainEvent);
        JSONAssert.assertEquals(
                "{\"timestamp\":1, \"dealerId\":1}",
                json,
                false
        );

        DealerCreated domainEventFromJson = JacksonUtils.deSerialize(json, DealerCreated.class);
        assertEquals(1, domainEventFromJson.getDealerId());
    }

}
