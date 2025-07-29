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
package org.ceskaexpedice.processplatform.worker.utils;

import org.ceskaexpedice.processplatform.common.ApplicationException;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Utils
 * @author ppodsednik
 */
public final class Utils {

    private Utils() {}

    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
            if (!json.isEmpty()) {
                String[] entries = json.split(",");
                for (String entry : entries) {
                    String[] keyValue = entry.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                        String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
                        map.put(key, value);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Returns PID of process
     *
     * @return
     */
    public static String getPid() {
        String pid = null;
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String[] split = name.split("@");
        if ((split != null) && (split.length > 1)) {
            pid = split[0];
        }
        return pid;
    }

    public static void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
