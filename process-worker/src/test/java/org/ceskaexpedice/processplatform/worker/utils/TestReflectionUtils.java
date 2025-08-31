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
package org.ceskaexpedice.processplatform.worker.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * TestReflectionUtils
 *
 * @author ppodsednik
 */
public class TestReflectionUtils {

    @Test
    public void testParametersTypeMapping() throws InvocationTargetException, IllegalAccessException {
        Map<String, String> payload = new HashMap<>();
        payload.put("stringP", "stringPVal");
        payload.put("booleanP", "true");
        payload.put("numberP", "123");
        payload.put("dateP", "2025-08-08");
        ReflectionUtils.MethodType processMethod = ReflectionUtils.annotatedMethodType(Plugin1.class);
        Object[] params = ReflectionUtils.map(processMethod.getMethod(), new String[]{}, payload);
        Assertions.assertEquals("stringPVal", params[0]);
        Assertions.assertEquals(true, params[1]);
        Assertions.assertEquals(123, params[2]);
        processMethod.getMethod().invoke(null, params);
    }

}
