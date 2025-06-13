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
package org.ceskaexpedice.processplatform.worker.plugin.executor;

import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.PluginInfo;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.PLUGIN_PATH_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TestPluginJvmLauncher
 *
 * @author ppodsednik
 */
public class TestPluginJvmLauncher {

    @Test
    public void testPlugin1JvmLauncher() {
        URL resource = getClass().getClassLoader().getResource("plugins");
        assertNotNull(resource, "Plugins directory not found in test resources");

        Properties props = new Properties();
        props.put(PLUGIN_PATH_KEY, resource.getFile());
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(props);
        String starterClasspath = System.getProperty("java.class.path");
        workerConfiguration.set(WorkerConfiguration.STARTER_CLASSPATH_KEY, starterClasspath);

        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xmx1024m");
        jvmArgs.add("-Xms256m");
        jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

        Map<String,String> payload = new HashMap<>();
        payload.put("name","Petr");
        payload.put("surname","Harasil");

        ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
                UUID.randomUUID(),
                "testPlugin1",
                "org.ceskaexpedice.processplatform.testplugin1.TestPlugin1",
                payload,
                jvmArgs);

        PluginJvmLauncher.launchJvm(scheduledProcessTO, workerConfiguration);
        // TODO add asserts
    }

    @Test
    public void testPlugin2JvmLauncher() {
        URL resource = getClass().getClassLoader().getResource("plugins");
        assertNotNull(resource, "Plugins directory not found in test resources");

        Properties props = new Properties();
        props.put(PLUGIN_PATH_KEY, resource.getFile());
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(props);
        String starterClasspath = System.getProperty("java.class.path");
        workerConfiguration.set(WorkerConfiguration.STARTER_CLASSPATH_KEY, starterClasspath);

        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xmx1024m");
        jvmArgs.add("-Xms256m");
        //jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

        Map<String,String> payload = new HashMap<>();

        ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
                UUID.randomUUID(),
                "testPlugin2",
                "org.ceskaexpedice.processplatform.testplugin2.TestPlugin2",
                payload,
                jvmArgs);

        PluginJvmLauncher.launchJvm(scheduledProcessTO, workerConfiguration);
        // TODO add asserts
    }

}
