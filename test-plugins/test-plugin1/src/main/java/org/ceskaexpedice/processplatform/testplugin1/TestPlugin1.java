package org.ceskaexpedice.processplatform.testplugin1;
import org.apache.commons.lang3.StringUtils;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;
import org.ceskaexpedice.processplatform.common.entity.ScheduleProcess;

import java.util.HashMap;

public class TestPlugin1 {

    @ProcessMethod
    public static void process(@ParameterName("name") String name, @ParameterName("surname") String surname) {
        System.out.println("TestPlugin1.process:" + name + "," + surname);
        String joined = join(name, surname);

        PluginContext pluginContext = PluginContextHolder.getContext();
        pluginContext.updateProcessName(joined);
        ScheduleProcess scheduleProcess = new ScheduleProcess("testPlugin2", "testPlugin2", new HashMap<>());
        pluginContext.scheduleProcess(scheduleProcess);
    }

    private static String join(String name, String surname) {
        return StringUtils.join(name, surname);
    }
}