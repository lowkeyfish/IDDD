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

import java.util.HashMap;
import java.util.Map;

public class BusinessRuleException extends RuntimeException {
    private int code;
    private Map<String, Object> details;

    public BusinessRuleException(String message) {
        this(message, 400, new HashMap<>());
    }

    public BusinessRuleException(String message, Map<String, Object> details) {
        this(message, 400, details);
    }

    public BusinessRuleException(String message, int code) {
        this(message, code, new HashMap<>());
    }

    public BusinessRuleException(String message, int code, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = details;
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

    public Map<String, Object> getDetails() {
        return details;
    }
}
