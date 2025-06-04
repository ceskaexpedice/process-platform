package org.ceskaexpedice.processplatform.testplugin1;
import org.apache.commons.lang3.StringUtils;

public class TestPlugin1 {

    public static void main(String[] args) {
        System.out.printf("Hello and welcome module1!" + isInputValid("pepo"));

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }

    private static boolean isInputValid(String input) {
        return !StringUtils.isBlank(input);
    }
}