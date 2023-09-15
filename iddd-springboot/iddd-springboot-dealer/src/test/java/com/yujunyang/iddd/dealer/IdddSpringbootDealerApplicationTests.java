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

package com.yujunyang.iddd.dealer;

import java.time.LocalDate;

import com.yujunyang.iddd.common.utils.TripleDESUtils;
import com.yujunyang.iddd.dealer.domain.activity.ActivityRegistrationVerificationCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IdddSpringbootDealerApplicationTests {

    @Test
    public void activityRegistrationVerificationCode() {
        ActivityRegistrationVerificationCodeService service = new ActivityRegistrationVerificationCodeService();
        System.out.println(service.generate());
        LocalDate.now().plusDays(1);
    }

    @Test
    public void encrypt() {
        String key = "zmsgDVSDPm5KyMj71rxkRU+UFuXy3ykT";
        String iv = TripleDESUtils.iv();
        String encrypt = TripleDESUtils.encrypt("123123", key, iv);
        System.out.println(encrypt);

        String decrypt = TripleDESUtils.decrypt(encrypt, key, iv);
        System.out.println(decrypt);
    }

}
