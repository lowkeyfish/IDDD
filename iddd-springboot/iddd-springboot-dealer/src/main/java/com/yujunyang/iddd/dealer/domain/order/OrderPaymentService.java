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

package com.yujunyang.iddd.dealer.domain.order;

import com.yujunyang.iddd.common.exception.BusinessRuleException;
import com.yujunyang.iddd.common.utils.CheckUtils;
import com.yujunyang.iddd.dealer.domain.payment.PaymentOrder;
import com.yujunyang.iddd.dealer.domain.payment.PaymentStatusType;
import org.springframework.stereotype.Service;

@Service
public class OrderPaymentService {


    public void markAsPaymentInitiated(
            AbstractOrder order,
            PaymentOrder paymentOrder) {
        CheckUtils.notNull(order, "order 必须不为 null");
        CheckUtils.notNull(paymentOrder, "paymentOrder 必须不为 null");

        CheckUtils.isTrue(
                PaymentStatusType.INITIATED.equals(paymentOrder.status()),
                new BusinessRuleException(
                        "支付单当前状态"
                )
        );
    }

}
