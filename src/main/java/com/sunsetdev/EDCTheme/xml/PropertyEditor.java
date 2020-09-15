package main.java.com.sunsetdev.EDCTheme.xml;

import java.io.*;
import java.util.Properties;

public class PropertyEditor {
    private final File xmlFile = new File(System.getProperty("user.home") +
            File.separator + "edcpt" + File.separator + "conf.xml");
    private final Properties props = new Properties();


    PropertyEditor() {
        if (!xmlFile.exists())
            createFile();
        readFile();
    }

    public void saveToConf(String key, String value) {
        FileOutputStream out = null;
        try (FileInputStream in = new FileInputStream(xmlFile)) {
            props.loadFromXML(in);
            props.setProperty(key, value);
            out = new FileOutputStream(xmlFile);
            props.storeToXML(out, "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getKey(String key) throws IOException {
        if (props.getProperty(key) == null)
            throw new IOException(key + " does not exist in config!");
        return props.getProperty(key);
    }

    private void readFile(){
        try (FileInputStream in = new FileInputStream(xmlFile)){
            props.loadFromXML(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        try {
            if (!xmlFile.getParentFile().mkdirs() || xmlFile.createNewFile() )
                throw new IOException("Error creating new file: " + xmlFile.getAbsolutePath());
            initXmlFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initXmlFile() throws IOException {
        try (OutputStream out = new FileOutputStream(xmlFile)) {
            props.setProperty("on_time", "false");
            props.storeToXML(out, "");
        }
    }
}
