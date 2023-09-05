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

public class Model {
    private ModelId id;
    private BrandId brandId;
    private ManufacturerId manufacturerId;
    private String name;
    private String image;
    private int priceMin;
    private int priceMax;

    public Model(
            ModelId id,
            BrandId brandId,
            ManufacturerId manufacturerId,
            String name,
            String image,
            int priceMin,
            int priceMax) {
        this.id = id;
        this.brandId = brandId;
        this.manufacturerId = manufacturerId;
        this.name = name;
        this.image = image;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
    }

    public ModelId getId() {
        return id;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public ManufacturerId getManufacturerId() {
        return manufacturerId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getPriceMin() {
        return priceMin;
    }

    public int getPriceMax() {
        return priceMax;
    }
}
