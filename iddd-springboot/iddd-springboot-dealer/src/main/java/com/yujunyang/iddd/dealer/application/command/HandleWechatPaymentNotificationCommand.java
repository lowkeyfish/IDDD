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

package com.yujunyang.iddd.dealer.application.command;

public class HandleWechatPaymentNotificationCommand {
    private String id;
    private String createTime;
    private String eventType;
    private String resourceType;
    private String summary;
    private String resourceAlgorithm;
    private String resourceCiphertext;
    private String resourceAssociatedData;
    private String resourceOriginalType;
    private String resourceNonce;

    public HandleWechatPaymentNotificationCommand(
            String id,
            String createTime,
            String eventType,
            String resourceType,
            String summary,
            String resourceAlgorithm,
            String resourceCiphertext,
            String resourceAssociatedData,
            String resourceOriginalType,
            String resourceNonce) {
        this.id = id;
        this.createTime = createTime;
        this.eventType = eventType;
        this.resourceType = resourceType;
        this.summary = summary;
        this.resourceAlgorithm = resourceAlgorithm;
        this.resourceCiphertext = resourceCiphertext;
        this.resourceAssociatedData = resourceAssociatedData;
        this.resourceOriginalType = resourceOriginalType;
        this.resourceNonce = resourceNonce;
    }

    public String getId() {
        return id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getEventType() {
        return eventType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getSummary() {
        return summary;
    }

    public String getResourceAlgorithm() {
        return resourceAlgorithm;
    }

    public String getResourceCiphertext() {
        return resourceCiphertext;
    }

    public String getResourceAssociatedData() {
        return resourceAssociatedData;
    }

    public String getResourceOriginalType() {
        return resourceOriginalType;
    }

    public String getResourceNonce() {
        return resourceNonce;
    }
}
