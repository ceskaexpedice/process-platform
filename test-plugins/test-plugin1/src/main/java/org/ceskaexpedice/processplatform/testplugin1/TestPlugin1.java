package org.ceskaexpedice.processplatform.testplugin1;
import org.apache.commons.lang3.StringUtils;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;
import org.ceskaexpedice.processplatform.common.model.ScheduleSubProcess;

import java.util.HashMap;

public class TestPlugin1 {

    @ProcessMethod
    public static void createFullName(@ParameterName("name") String name, @ParameterName("surname") String surname) {
        System.out.println("TestPlugin1.createFullName:name-" + name + ",surname-" + surname);
        String fullName = join(name, surname);
        // we might do something with "fullName" here like write it to the index

        // we can use the context too...
        PluginContext pluginContext = PluginContextHolder.getContext();
        pluginContext.updateProcessName("NewProcessName-" + fullName);
        ScheduleSubProcess scheduleSubProcess = new ScheduleSubProcess("testPlugin2", new HashMap<>());
        pluginContext.scheduleSubProcess(scheduleSubProcess);
    }

    private static String join(String name, String surname) {
        return StringUtils.join(name, surname);
    }
}