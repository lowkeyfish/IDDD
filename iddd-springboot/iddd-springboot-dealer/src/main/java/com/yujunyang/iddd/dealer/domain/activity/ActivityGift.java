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

package com.yujunyang.iddd.dealer.domain.activity;

import com.yujunyang.iddd.dealer.domain.gift.GiftId;

public class ActivityGift {
    private GiftId giftId;
    private int count;
    private int remainingCount;

    public ActivityGift(
            GiftId giftId,
            int count) {
        this(giftId, count, count);
    }

    public ActivityGift(
            GiftId giftId,
            int count,
            int remainingCount) {
        this.giftId = giftId;
        this.count = count;
        this.remainingCount = remainingCount;
    }

    public void receive() {
        if (remainingCount <= 0) {
            throw new UnsupportedOperationException("礼品已被领取完");
        }
        remainingCount--;
    }


    public GiftId getGiftId() {
        return giftId;
    }

    public int getCount() {
        return count;
    }

    public int getRemainingCount() {
        return remainingCount;
    }
}
