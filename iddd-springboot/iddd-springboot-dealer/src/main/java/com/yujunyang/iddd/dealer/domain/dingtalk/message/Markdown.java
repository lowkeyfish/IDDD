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

package com.yujunyang.iddd.dealer.domain.dingtalk.message;

public class Markdown extends AbstractMessage {
    private String title;
    private String text;

    public Markdown(At at, String title, String text) {
        super("markdown", at);
        CheckUtils.notBlank(title, "title 必须不为空");
        CheckUtils.notBlank(text, "text 必须不为空");
        this.title = title;
        this.text = text;
    }

    public Markdown(String title, String text) {
        this(At.none(), title, text);
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
