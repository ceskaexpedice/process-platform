package org.ceskaexpedice.processplatform.testplugin1;
import org.apache.commons.lang3.StringUtils;
import org.ceskaexpedice.processplatform.api.ProcessState;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessPlugin;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;

public class TestPlugin1 {

    @ProcessPlugin
    public static void process(@ParameterName("name") String name, @ParameterName("surname") String surname) {
        System.out.println("TestPlugin1.process:" + name + "," + surname);
        String joined = join(name, surname);

        PluginContext pluginContext = PluginContextHolder.getContext();
        pluginContext.updateProcessName(joined);
        pluginContext.updateProcessState(ProcessState.WARNING);
        pluginContext.scheduleProcess("SomeProcess"); // TODO
    }

    private static String join(String name, String surname) {
        return StringUtils.join(name, surname);
    }
}