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
package org.ceskaexpedice.processplatform.worker.plugin.utils;

import org.ceskaexpedice.processplatform.worker.plugin.entity.PluginInfo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

/**
 * TestPluginUtils
 *
 * @author ppodsednik
 */
public class TestPluginUtils {

    @Test
    public void testPluginsLoader() {
        String pluginPath = "C:\\projects\\process-platform\\process-worker\\src\\test\\resources\\plugins";
        List<PluginInfo> pluginInfos = PluginsLoader.load(new File(pluginPath));
        System.out.println("pluginInfos: " + pluginInfos);
    }

    @Test
    public void testUff() {
        String pluginPath = "C:\\projects\\process-platform\\process-worker\\src\\test\\resources\\plugins";
        List<PluginInfo> pluginInfos = PluginsLoader.load(new File(pluginPath));
        //ProcessTask processTask = new ProcessTask(); // this task comes from the manager
        PluginJvmLauncher.launchJvm(null);
        System.out.println("pluginInfos: " + pluginInfos);
    }

}
