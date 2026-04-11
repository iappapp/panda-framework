package com.github.iappapp.panda.utils;

import org.junit.Test;

import java.util.Properties;

/**
 * @author iappapp
 * @date 2025/6/24
 * @description TODO
 */
public class PropertyUtilTests {
    @Test
    public void getProperties() {
        Properties properties = PropertyUtil.getProperties("resource.txt");
        System.out.println(properties);
        String path = PropertyUtil.getRealPath4ClassPath();
        System.out.println(path);
    }
}
