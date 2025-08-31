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
package org.ceskaexpedice.processplatform.worker.api.service.os;

import org.apache.commons.lang3.SystemUtils;
import org.ceskaexpedice.processplatform.worker.api.service.os.unix.UnixHandler;
import org.ceskaexpedice.processplatform.worker.api.service.os.windows.WindowsHandler;

/**
 * OSHandlerFactory
 * @author ppodsednik
 */
public final class OSHandlerFactory {

    private OSHandlerFactory() {
    }

    public static OSHandler createOSHandler(String pid) {
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX) {
            return new UnixHandler(pid);
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return new WindowsHandler(pid);
        } else throw new UnsupportedOperationException("unsupported OS");
    }
}
