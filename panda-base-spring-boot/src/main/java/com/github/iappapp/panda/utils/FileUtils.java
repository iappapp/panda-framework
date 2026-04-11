/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.ClassPathResource
 */
package com.github.iappapp.panda.utils;

import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static InputStream loadClassPathFile(String classPathUrl) {
        try {
            ClassPathResource resource = new ClassPathResource(classPathUrl);
            return resource.getInputStream();
        }
        catch (IOException e) {
            log.error("file is not exist with path {}", classPathUrl);
            return null;
        }
    }

    public static String readToJsonStr(String classPathUrl) {
        InputStream inputStream = FileUtils.loadClassPathFile(classPathUrl);
        if (inputStream == null) {
            return null;
        }
        return FileUtils.readToJsonStr(inputStream);
    }

    public static <T> T readJsonFileToObj(String classPathUrl, Class<T> cla) {
        String jsonStr = FileUtils.readToJsonStr(classPathUrl);
        return (T)JSONObject.parseObject(jsonStr, cla);
    }


    public static String readToJsonStr(InputStream inputStream) {
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(inputStream, "UTF-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        }
        catch (Exception e) {
            log.error("===== Exception happened : =====", e);
        }
        finally {
            if (scanner != null) {
                scanner.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    log.error("===== IOException happened : =====", e);
                }
            }
        }
        return buffer.toString();
    }


    public static void inputStream2File(InputStream is, File file) {
        FileOutputStream os = null;
        try {
            int byteRead;
            os = new FileOutputStream(file);
            int len = 2048;
            byte[] buffer = new byte[len];
            while ((byteRead = is.read(buffer, 0, len)) != -1) {
                ((OutputStream)os).write(buffer, 0, byteRead);
            }
        }
        catch (FileNotFoundException e) {
            log.error("FileNotFoundException happened : ", e);
        }
        catch (IOException e) {
            log.error("IOException happened : ", e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    log.error("IOException happened : ", e);
                }
            }
            if (os != null) {
                try {
                    ((OutputStream)os).close();
                }
                catch (IOException e) {
                    log.error("IOException happened : ", e);
                }
            }
        }
    }
}

