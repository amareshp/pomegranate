package com.qatrend.pomegranate.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.qatrend.pomegranate.util.StringUtil;

public class EnvUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    public static String propsFilePath = "src/test/resources/config/env.properties";;

    public static String getEnvOrProperty(String propName) {
        String value = System.getenv(propName);
        if (value == null) {
            value = System.getProperty(propName);
        }
        if (value == null) {
            value = System.getenv(propName.toLowerCase());
        }
        if (value == null) {
            value = System.getenv(propName.toUpperCase());
        }
        if (value == null) {
            value = System.getProperty(propName.toLowerCase());
        }
        if (value == null) {
            value = System.getProperty(propName.toUpperCase());
        }
        return value;
    }

    public static String getProperty(String propFilePath, String propName) {
        InputStream input = null;
        Properties prop = new Properties();
        String value = null;
        try {
            input = new FileInputStream(propFilePath);
            prop.load(input);
            value = prop.getProperty(propName);
            if (value == null) {
                value = prop.getProperty(propName.toLowerCase());
            }
            if (value == null) {
                value = prop.getProperty(propName.toUpperCase());
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return value;
    }

    public static void setProperty(String propFilePath, String propName, String propValue) {
        boolean isPresent = false;
        String line = propName + "=" + propValue + "\n";
        try {
            List<String> lines = FileUtils.readLines(new File(propFilePath));
            for (int i = 0; i < lines.size(); i++) {
                if ((!StringUtil.nullOrEmpty(lines.get(i))) && (lines.get(i).contains("="))) {
                    int equalsAt = lines.get(i).indexOf("=");
                    String property = lines.get(i).substring(0, equalsAt).trim();
                    if (property.equals(propName)) {
                        isPresent = true;
                        lines.set(i, line);
                    }
                }
            }
            if (!isPresent) {
                lines.add(line);
            }
            FileUtils.writeLines(new File(propFilePath), lines);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static String getEnvSpecificProperty(String propName) {
        String value = getProperty(propsFilePath, propName);
        return value;
    }

    public static String getPropertyFilePath() {
        return propsFilePath;
    }

    public static void setPropertyFilePath(String filePath) {
        propsFilePath = filePath;
    }
    
}
