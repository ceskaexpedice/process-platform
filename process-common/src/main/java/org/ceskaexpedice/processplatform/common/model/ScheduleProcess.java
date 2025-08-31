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
package org.ceskaexpedice.processplatform.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * ScheduleProcess
 * @author ppodsednik
 */
public abstract class ScheduleProcess {
    private String profileId;
    private Map<String, String> payload =  new HashMap<>();

    public ScheduleProcess() {
    }

    public ScheduleProcess(String profileId, Map<String, String> payload) {
        this.profileId = profileId;
        this.payload = payload;
    }

    public String getProfileId() {
        return profileId;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }
}
