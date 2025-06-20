/*
 * Copyright © 2021 Accenture and/or its affiliates. All Rights Reserved.
 * Permission to any use, copy, modify, and distribute this software and
 * its documentation for any purpose is subject to a licensing agreement
 * duly entered into with the copyright owner or its affiliate.
 * All information contained herein is, and remains the property of Accenture
 * and/or its affiliates and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Accenture and/or
 * its affiliates and its suppliers and may be covered by one or more patents
 * or pending patent applications in one or more jurisdictions worldwide,
 * and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Accenture and/or its affiliates.
 */
package org.ceskaexpedice.processplatform.worker.plugin.loader;

import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.worker.Constants;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TestPluginsLoader
 *
 * @author ppodsednik
 */
public class TestPluginsLoader {

    @Test
    public void testPluginsLoader() {
        URL resource = getClass().getClassLoader().getResource("plugins");
        File pluginDir = new File(resource.getFile());
        List<PluginInfo> pluginInfos = PluginsLoader.load(pluginDir);

        // check both plugins were loaded
        assertEquals(2, pluginInfos.size());
        PluginInfo testPlugin1 = null;
        PluginInfo testPlugin2 = null;
        for (PluginInfo pluginInfo : pluginInfos) {
            if(pluginInfo.getPluginId().equals(Constants.PLUGIN1_ID)){
                testPlugin1 = pluginInfo;
            }else{
                testPlugin2 = pluginInfo;
            }
        }
        assertNotNull(testPlugin1);
        assertNotNull(testPlugin2);

        // check profiles were merged:
        // testPlugin1.json from jar:
        /*
        [
          {
            "profileId": "testPlugin1-big",
            "jvmArgs": ["-Xms1g","-Xmx32g"]
          },
          {
            "profileId": "testPlugin1-small",
            "jvmArgs": ["-Xms1g", "-Xmx4g"]
          }
        ]
         */
        // testPlugin1.json from test/resources/plugins/testPlugin1:
        /*
        [
          {
            "profileId": "testPlugin1-big",
            "jvmArgs": ["-Xms1g", "-Xmx64g"]
          },
          {
            "profileId": "testPlugin1-smallest",
            "jvmArgs": ["-Xms1g","-Xmx2g"]
          }
        ]
         */
        assertEquals(3, testPlugin1.getProfiles().size());
        PluginProfile profileBiggest = null;
        for (PluginProfile profile : testPlugin1.getProfiles()) {
            if(profile.getProfileId().equals(Constants.PLUGIN1_PROFILE_BIG)){
                profileBiggest = profile;
            }
        }
        assertNotNull(profileBiggest);
        boolean biggestXmx = false;
        for (String jvmArg : profileBiggest.getJvmArgs()) {
            if(jvmArg.equals("-Xmx64g")){
                biggestXmx = true;
            }
        }
        assertTrue(biggestXmx); // make sure it has been overridden in the process

        assertEquals(1, testPlugin2.getProfiles().size());
    }

}
