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
package org.ceskaexpedice.processplatform.worker.plugin;

import java.util.List;
import java.util.Map;

public class PluginProfile {
    private final String profileId;        // e.g. "import-cgi"
    //private final String pluginId;         // base plugin, e.g. "import"
    private final Map<String, String> staticParams;  // e.g. {"pars": "ADD"}
    private final List<String> jvmArgs;    // e.g. ["-Xmx1G"]

    public PluginProfile(String profileId, String pluginId, Map<String, String> staticParams, List<String> jvmArgs) {
        this.profileId = profileId;
        this.staticParams = staticParams;
        this.jvmArgs = jvmArgs;
    }

    /*
    public class PluginProfile {
    private final String profileId;
    private final List<String> staticParams;
    private final List<String> jvmArgs;

    public PluginProfile(String profileId, List<String> staticParams, List<String> jvmArgs) {
        this.profileId = profileId;
        this.staticParams = staticParams;
        this.jvmArgs = jvmArgs;
    }

    public String getProfileId() {
        return profileId;
    }

    public List<String> getStaticParams() {
        return staticParams;
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }
}


     */

    // Getters...
}
/*
[
  {
    "pluginId": "import",
    "description": "Import plugin",
    "mainClass": "cz.kramerius.plugins.importer.Main",
    "profiles": [
      {
        "profileId": "import-cgi",
        "pluginId": "import",
        "staticParams": {
          "pars": "ADD"
        },
        "jvmArgs": [
          "-Xmx1G"
        ]
      },
      {
        "profileId": "import-cgi-remove",
        "pluginId": "import",
        "staticParams": {
          "pars": "REMOVE"
        },
        "jvmArgs": [
          "-Xmx1G"
        ]
      }
    ]
  }
]


 */