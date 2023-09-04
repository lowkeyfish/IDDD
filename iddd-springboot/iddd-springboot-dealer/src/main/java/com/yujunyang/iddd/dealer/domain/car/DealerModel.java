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

public class DealerModel {
    private DealerId dealerId;
    private ModelId modelId;
    private boolean deleted;


    public DealerModel(
            DealerId dealerId,
            ModelId modelId) {
        this.dealerId = dealerId;
        this.modelId = modelId;
        this.deleted = false;

        DomainEventPublisher.instance().publish(new DealerModelAdded(
                DateTimeUtilsEnhance.epochMilliSecond(),
                dealerId.getId(),
                modelId.getId()
        ));
    }

    public void delete() {
        deleted = true;

        DomainEventPublisher.instance().publish(new DealerModelDeleted(
                DateTimeUtilsEnhance.epochMilliSecond(),
                dealerId.getId(),
                modelId.getId()
        ));
    }

    public DealerId getDealerId() {
        return dealerId;
    }

    public ModelId getModelId() {
        return modelId;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
