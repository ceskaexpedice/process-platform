/*
 * Copyright (C) 2025 Inovatika
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ceskaexpedice.processplatform.common.utils;

import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.ErrorCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public final class DateUtils {

    private DateUtils() {}

    public static String toFormattedStringOrNull(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime);
        }
    }

    public static LocalDateTime parseLocalDateTime(String string) {
        if (string == null) {
            return null;
        } else {
            try {
                return LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new BusinessLogicException(String.format("incorrect date-time format '%s'", string), ErrorCode.INVALID_INPUT);
            }
        }
    }

    public static Date convert(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Date date = Date.from(localDateTime.atZone(zone).toInstant());
        return date;
    }

}
