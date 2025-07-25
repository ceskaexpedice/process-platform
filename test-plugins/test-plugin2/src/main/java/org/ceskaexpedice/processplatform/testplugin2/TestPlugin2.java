package org.ceskaexpedice.processplatform.testplugin2;

import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;

/**
 * TestPlugin2
 * @author ppodsednik
 */
public class TestPlugin2 {

    @ProcessMethod
    public static void foo() {
        System.out.println("TestPlugin2.foo");
    }

}