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
import com.yujunyang.iddd.dealer.domain.dealer.Dealer;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;
import com.yujunyang.iddd.dealer.domain.dealer.DealerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerVariantFactory {
    private DealerRepository dealerRepository;
    private VariantService variantService;
    private VariantSalePriceMaximumDiscountCheckService variantSalePriceMaximumDiscountCheckService;

    @Autowired
    public DealerVariantFactory(
            DealerRepository dealerRepository,
            VariantService variantService,
            VariantSalePriceMaximumDiscountCheckService variantSalePriceMaximumDiscountCheckService) {
        this.dealerRepository = dealerRepository;
        this.variantService = variantService;
        this.variantSalePriceMaximumDiscountCheckService = variantSalePriceMaximumDiscountCheckService;
    }

    public DealerVariant newDealerVariant(
            DealerId dealerId,
            VariantId variantId,
            int salePrice) {
        Dealer dealer = dealerRepository.findById(dealerId);
        CheckUtils.notNull(dealer, "DealerId({0}) 无效", dealerId);

        Variant variant = variantService.findById(variantId);
        CheckUtils.notNull(variant, "VariantId({{0}) 无效", variantId);

        variantSalePriceMaximumDiscountCheckService.checkDiscount(variant, salePrice);
        return new DealerVariant(
                dealerId,
                variant.getModelId(),
                variantId,
                salePrice,
                variant.getManufacturerId(),
                variant.getBrandId());
    }
}
