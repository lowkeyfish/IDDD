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

package com.yujunyang.iddd.common.exception;

public class BusinessRuleException extends RuntimeException {
    private int code;

    public BusinessRuleException(String message) {
        this(message, 400);
    }

    public BusinessRuleException(int code) {
        this.code = code;
    }

    public BusinessRuleException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessRuleException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public BusinessRuleException(Throwable cause) {
        super(cause);
    }

    public BusinessRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
