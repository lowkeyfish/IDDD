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
import org.springframework.stereotype.Service;

@Service
public class DealerModelFactory {
    private DealerRepository dealerRepository;
    private ModelService modelService;

    public DealerModelFactory(
            DealerRepository dealerRepository,
            ModelService modelService) {
        this.dealerRepository = dealerRepository;
        this.modelService = modelService;
    }

    public DealerModel createDealerModel(
            DealerId dealerId,
            ModelId modelId) {
        Dealer dealer = dealerRepository.findById(dealerId);
        CheckUtils.notNull(dealer, "DealerId({0}) 无效", dealerId);

        Model model = modelService.findById(modelId);
        CheckUtils.notNull(model, "ModelId({0}) 无效", modelId);

        return new DealerModel(dealerId, modelId, model.getManufacturerId(), model.getBrandId());
    }
}
