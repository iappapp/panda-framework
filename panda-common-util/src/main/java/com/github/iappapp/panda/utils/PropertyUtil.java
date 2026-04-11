package com.github.iappapp.panda.utils;

/**
 * @author liush2
 * @date 2019/9/6 11:04
 * @remarks
 */
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Slf4j
public class PropertyUtil {

    public PropertyUtil() {
    }

    public static String getRealPath4ClassPath() {
        String path = PropertyUtil.class.getResource("/" + PropertyUtil.class.getCanonicalName().replace('.', '/') + ".class").getPath();
        if (path == null) {
            path = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
        } else {
            path = path.substring(0, path.indexOf(PropertyUtil.class.getCanonicalName().replace('.', '/') + ".class"));
        }

        if (path.endsWith("/classes/")) {
            return path;
        } else if (path.endsWith(".jar!/") && path.startsWith("file:")) {
            path = path.substring(5);
            path = path.substring(0, path.lastIndexOf(".jar!/"));
            path = path.substring(0, path.lastIndexOf("/") + 1);
            return path.replace("/lib/", "/classes/");
        } else {
            return path;
        }
    }

    public static Properties getProperties(String fileName) {
        Properties p = new Properties();
        BufferedInputStream in = null;

        try {
            File file = new File(fileName);
            if (!file.exists()) {
                fileName = getRealPath4ClassPath() + fileName;
            }

            in = new BufferedInputStream(new FileInputStream(fileName));
            if (fileName.toLowerCase().endsWith(".xml")) {
                p.loadFromXML(in);
            } else {
                p.load(in);
            }
        } catch (Exception ex) {
            log.error("getProperties fileName={} ex={}", fileName, ex.getMessage());
            try {
                log.info("getProperties fileName={}", fileName.split("!/BOOT-INF/classes")[1]);
                InputStream inputStream = PropertyUtil.class.getResourceAsStream(fileName.split("!/BOOT-INF/classes")[1]);
                Properties properties = new Properties();
                properties.load(new InputStreamReader(inputStream, "UTF-8"));
                if (inputStream != null) {
                    inputStream.close();
                }
                return properties;
            } catch (Exception ignore) {
                try {
                    log.info("getProperties fileName={}", fileName.split("/classes")[1]);
                    InputStream inputStream = PropertyUtil.class.getResourceAsStream(fileName.split("/classes")[1]);
                    Properties properties = new Properties();
                    properties.load(new InputStreamReader(inputStream, "UTF-8"));
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    return properties;
                } catch (Exception innerEx) {
                    log.info("getProperties get ignore={}", innerEx.getMessage());
                }
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignore) {

            }

        }

        return p;
    }

    public static int getIntProperty(Properties p, String key, int defaultValue) {
        if (p == null) {
            return defaultValue;
        } else {
            String ret = p.getProperty(key);
            return StringUtils.isEmpty(ret) ? defaultValue : Integer.parseInt(ret);
        }
    }

    public static boolean getBooleanProperty(Properties p, String key, boolean defaultValue) {
        if (p == null) {
            return defaultValue;
        } else {
            String value = p.getProperty(key);
            if ("true".equalsIgnoreCase(value)) {
                return true;
            } else {
                return "false".equalsIgnoreCase(value) ? false : defaultValue;
            }
        }
    }

    public static void saveProperties(Properties p, String fileName) {
        saveProperties(p, fileName, (String)null, "UTF-8");
    }

    public static void saveProperties(Properties p, String fileName, String comment) {
        saveProperties(p, fileName, comment, "UTF-8");
    }

    public static void savePropertiesXml(Properties p, String fileName, String encoding) {
        saveProperties(p, fileName, (String)null, encoding);
    }

    public static void saveProperties(Properties p, String fileName, String comment, String encoding) {
        BufferedOutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(fileName));
            if (fileName.toLowerCase().endsWith(".xml")) {
                p.storeToXML(out, comment, encoding);
            } else {
                p.store(out, comment);
            }
        } catch (Exception exception) {
            log.error("Save properties to " + fileName + ",Exception: " + exception.getMessage());
            exception.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }

    }

    public static String getProperty(String fileName, String key) {
        String sRet = null;

        try {
            PropertyResourceBundle configBundle = (PropertyResourceBundle)ResourceBundle.getBundle(fileName);
            sRet = configBundle.getString(key);
        } catch (Exception exception) {
            log.error("Read " + fileName + " " + key + " getProperty Exception : " + exception.getMessage());
            exception.printStackTrace();
        }

        return sRet;
    }

    public static int getIntProperty(String fileName, String key, int defaultValue) {
        try {
            PropertyResourceBundle configBundle = (PropertyResourceBundle)ResourceBundle.getBundle(fileName);
            String sRet = configBundle.getString(key);
            return StringUtils.isEmpty(sRet) ? defaultValue : Integer.parseInt(sRet);
        } catch (Exception exception) {
            log.error("Read " + fileName + " " + key + " getProperty Exception : " + exception.getMessage());
            exception.printStackTrace();
            return defaultValue;
        }
    }


}
