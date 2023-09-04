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

public class Variant {
    private VariantId id;
    private String name;
    private String price;
    private BrandId brandId;
    private ManufacturerId manufacturerId;
    private ModelId modelId;

    public Variant(
            VariantId id,
            String name,
            String price,
            BrandId brandId,
            ManufacturerId manufacturerId,
            ModelId modelId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brandId = brandId;
        this.manufacturerId = manufacturerId;
        this.modelId = modelId;
    }

    public VariantId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public ManufacturerId getManufacturerId() {
        return manufacturerId;
    }

    public ModelId getModelId() {
        return modelId;
    }
}
