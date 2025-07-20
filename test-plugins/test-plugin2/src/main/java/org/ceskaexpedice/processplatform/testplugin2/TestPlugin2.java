package org.ceskaexpedice.processplatform.testplugin2;

import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;

public class TestPlugin2 {

    @ProcessMethod
    public static void foo() {
        System.out.println("TestPlugin2.foo");

        // TODO PluginContext pluginContext = PluginContextHolder.getContext();
    }

    /*
    private static boolean isInputValid(String input) {
        return !StringUtils.isBlank(input);
    }

     */
}