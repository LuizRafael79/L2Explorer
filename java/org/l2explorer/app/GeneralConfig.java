package org.l2explorer.app;

import java.io.*;
import java.util.Properties;

public class GeneralConfig {
    private static final String CONFIG_FILE = "l2explorer.ini";
    private static final Properties props = new Properties();

    static {
        load();
    }

    private static void load() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            // Arquivo não existe ainda, usa defaults
        }
    }

    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "L2Explorer Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLastDirectory() {
        return props.getProperty("lastDirectory", System.getProperty("user.home"));
    }

    public static void setLastDirectory(String path) {
        props.setProperty("lastDirectory", path);
        save();
    }

    public static String getLastPackage() {
        return props.getProperty("lastPackage", "");
    }

    public static void setLastPackage(String path) {
        props.setProperty("lastPackage", path);
        save();
    }
}