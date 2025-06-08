package org.ceskaexpedice.processplatform.testplugin1;
import org.apache.commons.lang3.StringUtils;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessPlugin;

public class TestPlugin1 {

    public static void main(String[] args) {
        System.out.println("TestPlugin1.main:");

        for (int i = 0; i < args.length; i++) {
            System.out.println("i = " + args[i]);
        }
    }

    @ProcessPlugin
    public static void process(@ParameterName("name") String name, @ParameterName("surname") String surname) {
        System.out.println("TestPlugin1.process:" + name + ":" + surname);
    }

    private static boolean isInputValid(String input) {
        return !StringUtils.isBlank(input);
    }
}