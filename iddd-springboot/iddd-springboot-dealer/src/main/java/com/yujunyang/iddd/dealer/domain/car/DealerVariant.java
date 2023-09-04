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

import com.yujunyang.iddd.common.domain.event.DomainEventPublisher;
import com.yujunyang.iddd.common.utils.DateTimeUtilsEnhance;
import com.yujunyang.iddd.dealer.domain.dealer.DealerId;

public class DealerVariant {
    private DealerId dealerId;
    private ModelId modelId;
    private VariantId variantId;
    private int salePrice;
    private boolean deleted;

    public DealerVariant(
            DealerId dealerId,
            ModelId modelId,
            VariantId variantId,
            int salePrice,
            boolean deleted) {
        this.dealerId = dealerId;
        this.modelId = modelId;
        this.variantId = variantId;
        this.salePrice = salePrice;
        this.deleted = deleted;
    }

    public void adjustSalePrice(int price) {

        salePrice = price;

        DomainEventPublisher.instance().publish(new DealerVariantUpdated(
                DateTimeUtilsEnhance.epochMilliSecond(),
                dealerId.getId(),
                modelId.getId(),
                variantId.getId()
        ));
    }

    public void delete() {
        deleted = true;

        DomainEventPublisher.instance().publish(new DealerVariantDeleted(
                DateTimeUtilsEnhance.epochMilliSecond(),
                dealerId.getId(),
                modelId.getId(),
                variantId.getId()
        ));
    }

    public DealerId getDealerId() {
        return dealerId;
    }

    public ModelId getModelId() {
        return modelId;
    }

    public VariantId getVariantId() {
        return variantId;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
