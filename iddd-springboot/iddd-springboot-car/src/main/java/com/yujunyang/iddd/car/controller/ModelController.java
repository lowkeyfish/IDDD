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

package com.yujunyang.iddd.car.controller;

import java.util.Arrays;
import java.util.List;

import com.yujunyang.iddd.car.application.ModelQueryService;
import com.yujunyang.iddd.car.application.VariantQueryService;
import com.yujunyang.iddd.car.application.data.ModelViewModel;
import com.yujunyang.iddd.car.application.data.VariantViewModel;
import com.yujunyang.iddd.car.domain.model.ModelId;
import com.yujunyang.iddd.common.data.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/models")
public class ModelController {
    private ModelQueryService modelQueryService;
    private VariantQueryService variantQueryService;

    @Autowired
    public ModelController(
            ModelQueryService modelQueryService,
            VariantQueryService variantQueryService) {
        this.modelQueryService = modelQueryService;
        this.variantQueryService = variantQueryService;
    }

    @GetMapping("{modelId}")
    public RestResponse<ModelViewModel> findModelById(
            @PathVariable("modelId") long modelId) {
        return new RestResponse<>(modelQueryService.findById(ModelId.parse(modelId)));
    }

    @GetMapping("{modelId}/variants")
    public RestResponse<List<VariantViewModel>> findVariantByModelId(
            @PathVariable("modelId") long modelId) {
        return new RestResponse<>(variantQueryService.findByModelIds(Arrays.asList(ModelId.parse(modelId))));
    }
}
