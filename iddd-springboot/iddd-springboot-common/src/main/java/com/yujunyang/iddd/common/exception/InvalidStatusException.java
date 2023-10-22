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

package com.yujunyang.iddd.common.exception;

import java.text.MessageFormat;

public class InvalidStatusException extends Exception {
    private String currentStatus;
    private String action;

    public InvalidStatusException(String currentStatus, String action) {
        super(MessageFormat.format(
                "无效的状态({0}), 操作({1})在当前状态下不支持",
                currentStatus,
                action
        ));
        this.currentStatus = currentStatus;
        this.action = action;
    }

    public InvalidStatusException(String message, String currentStatus, String action) {
        super(message);
        this.currentStatus = currentStatus;
        this.action = action;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getAction() {
        return action;
    }
}
