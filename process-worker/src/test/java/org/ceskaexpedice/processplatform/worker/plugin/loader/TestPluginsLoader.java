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

import org.ceskaexpedice.processplatform.worker.plugin.PluginInfo;
import org.ceskaexpedice.processplatform.worker.plugin.PluginProfile;
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
        assertNotNull(resource, "Plugins directory not found in test resources");

        File pluginDir = new File(resource.getFile());
        List<PluginInfo> pluginInfos = PluginsLoader.load(pluginDir);
        assertEquals(2, pluginInfos.size());
        PluginInfo testPlugin1 = null;
        PluginInfo testPlugin2 = null;
        for (PluginInfo pluginInfo : pluginInfos) {
            if(pluginInfo.getPluginId().equals("testPlugin1")){
                testPlugin1 = pluginInfo;
            }else{
                testPlugin2 = pluginInfo;
            }
        }
        assertNotNull(testPlugin1);
        assertNotNull(testPlugin2);
        assertEquals(3, testPlugin1.getProfiles().size());
        PluginProfile profileBiggest = null;
        for (PluginProfile profile : testPlugin1.getProfiles()) {
            if(profile.getProfileId().equals("testPlugin1-big")){
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
        assertTrue(biggestXmx); // make sure it was overriden in the process

        assertEquals(1, testPlugin2.getProfiles().size());
    }

}
