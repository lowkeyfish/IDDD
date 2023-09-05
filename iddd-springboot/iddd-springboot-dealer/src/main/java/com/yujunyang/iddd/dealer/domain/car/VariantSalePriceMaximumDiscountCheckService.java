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

package com.yujunyang.iddd.dealer.domain.car;

import com.yujunyang.iddd.common.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VariantSalePriceMaximumDiscountCheckService {
    private VariantService variantService;

    @Autowired
    public VariantSalePriceMaximumDiscountCheckService(
            VariantService variantService) {
        this.variantService = variantService;
    }

    public void checkDiscount(Variant variant, int salePrice) {
        int suggestedPrice = variant.getPrice();
        CheckUtils.moreThan(suggestedPrice, salePrice, "售价超过指导价");

        int discount = suggestedPrice - salePrice;
        CheckUtils.isTrue(suggestedPrice * 0.2 >= discount, "优惠超过指导价 20%");
    }

    public void checkDiscount(VariantId variantId, int salePrice) {
        Variant variant = variantService.findById(variantId);
        CheckUtils.notNull(variant, "VariantId({0}) 无效", variantId);

        checkDiscount(variant, salePrice);
    }
}
