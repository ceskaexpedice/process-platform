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

import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.entity.PluginInfo;
import org.ceskaexpedice.processplatform.worker.plugin.entity.PluginInfoMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

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
    public void testPluginJvmLauncher() {
        Properties props = new Properties();
        props.put("pluginPath", "C:\\projects\\process-platform\\process-worker\\src\\test\\resources\\plugins");

        //props.put("processApiPath", "c:\\Users\\petr\\.m2\\repository\\org\\ceskaexpedice\\process-api\\1.0-SNAPSHOT\\process-api-1.0-SNAPSHOT.jar");


        WorkerConfiguration workerConfiguration = new WorkerConfiguration(props);
        //workerConfiguration.set("starter.classpath", "target/test-classes" + File.pathSeparator + "libs/*");
        String starterClasspath = System.getProperty("java.class.path");
        workerConfiguration.set("starter.classpath", starterClasspath);

        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xmx1024m");
        jvmArgs.add("-Xms256m");
        jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

        List<PluginInfo> pluginInfos = PluginsLoader.load(new File(workerConfiguration.get("pluginPath").toString()));
        for (PluginInfo pluginInfo : pluginInfos) {
            if(pluginInfo.getPluginId().equals("testPlugin1")){
                Map<String,String> payload = new HashMap<>();
                payload.put("name","Petr");
                payload.put("surname","Harasil");
                ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
                        UUID.randomUUID(),
                        pluginInfo.getPluginId(),
                        "profileId",
                        pluginInfo.getMainClass(),
                        jvmArgs,
                        payload);
                PluginJvmLauncher.launchJvm(scheduledProcessTO, workerConfiguration);
            }
        }

    }

}
