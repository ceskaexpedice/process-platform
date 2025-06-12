package org.ceskaexpedice.processplatform.testplugin2;
import org.apache.commons.lang3.StringUtils;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessPlugin;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;

public class TestPlugin2 {

    @ProcessPlugin
    public static void process() {
        System.out.println("TestPlugin2.process");

        PluginContext pluginContext = PluginContextHolder.getContext();
        pluginContext.scheduleProcess("SomeProcess");
    }

    private static boolean isInputValid(String input) {
        return !StringUtils.isBlank(input);
    }
}