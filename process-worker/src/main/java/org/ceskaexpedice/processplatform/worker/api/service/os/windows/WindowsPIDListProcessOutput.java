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
package org.ceskaexpedice.processplatform.worker.api.service.os.windows;

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.worker.api.service.os.windows.csv.CSVLexer;
import org.ceskaexpedice.processplatform.worker.api.service.os.windows.csv.CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * WindowsPIDListProcessOutput
 */
public class WindowsPIDListProcessOutput {
	
	public static List<String> pids(BufferedReader reader) throws IOException {
		List<String> pids = new ArrayList<String>();
		try {
			CSVParser parser = new CSVParser(new CSVLexer(reader));
			List file = parser.file();
			for (Object object : file) {
				List record = (List) object;
				if (record.size() > 1) {
					String pid = (String) record.get(1);
					if (pid.startsWith("\"")) {
						pid = pid.substring(1);
					} 
					if (pid.endsWith("\"")) {
						pid = pid.substring(0,pid.length()-1);
					}
					pids.add(pid);
				}
			}
			return pids;
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage(), e);
		}
	}
	
}
