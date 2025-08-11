package org.ceskaexpedice.processplatform.worker.utils;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;

import java.time.LocalDate;

/**
 * TestPlugin1
 * @author ppodsednik
 */
public class Plugin1 {

    @ProcessMethod
    public static void mainPluginMethod(
            @ParameterName("stringP") String stringP,
            @ParameterName("booleanP") Boolean booleanP,
            @ParameterName("numberP") Integer numberP,
            @ParameterName("dateP") LocalDate date
    ) {
        System.out.println("Plugin1.mainPluginMethod: stringP-" + stringP + ", booleanP-" + booleanP +
                ", numberP-" + numberP + ", dateP-" + date);
    }

}