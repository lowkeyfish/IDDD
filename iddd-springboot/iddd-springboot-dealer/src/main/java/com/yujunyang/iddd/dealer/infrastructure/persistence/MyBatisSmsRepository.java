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

package com.yujunyang.iddd.dealer.infrastructure.persistence;

import com.yujunyang.iddd.dealer.domain.sms.Sms;
import com.yujunyang.iddd.dealer.domain.sms.SmsId;
import com.yujunyang.iddd.dealer.domain.sms.SmsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisSmsRepository implements SmsRepository {
    @Override
    public SmsId nextId() {
        return null;
    }

    @Override
    public void save(Sms sms) {

    }

    @Override
    public Sms findById(SmsId id) {
        return null;
    }
}
