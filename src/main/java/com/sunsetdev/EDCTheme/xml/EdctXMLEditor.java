package main.java.com.sunsetdev.EDCTheme.xml;

import javafx.scene.control.Alert;
import main.java.com.sunsetdev.EDCTheme.model.Theme;
import main.java.com.sunsetdev.EDCTheme.model.exception.MatrixFormattingException;
import main.java.com.sunsetdev.EDCTheme.util.Util;
import org.jdom2.Document;
import org.jdom2.Element;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.text.ParseException;
import java.util.List;

//Elite Dangerous Cockpit Theme Xml Editor.
public class EdctXMLEditor {

    private final File themesXmlFile = new File("themes.xml");
    private File graphicsConfXmlFile;
    private final Document themesDocument = documentBuilder(themesXmlFile);
    private Document graphicsConfDocument;
    private final PropertyEditor propertyEditor = new PropertyEditor();

    
    public EdctXMLEditor() {
        importGraphicsConf();
    }

    private void backupGraphicsConf() throws IOException {
        File backup = new File(graphicsConfXmlFile + "-backup");
        if (backup.exists())
            return;
        try (FileInputStream fis = new FileInputStream(graphicsConfXmlFile);
             FileOutputStream fos = new FileOutputStream(backup)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    public void importGraphicsConf() {
        try {
            graphicsConfXmlFile = new File(propertyEditor.getKey("gconf"));
            graphicsConfDocument = documentBuilder(graphicsConfXmlFile);
            backupGraphicsConf();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void tryToCreateFile() {
        try {
            if (!themesXmlFile.isFile() && !themesXmlFile.createNewFile())
                throw new IOException("Error creating new file: " + themesXmlFile.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addThemeToXML(Theme theme) {
        Element savedThemes = themesDocument.getRootElement();
        Element themeToBeSaved = new Element("theme");
        themeToBeSaved.addContent(new Element("name").setText(theme.getName()));
        themeToBeSaved.addContent(new Element("red_matrix").setText(theme.getRedMatrix()));
        themeToBeSaved.addContent(new Element("green_matrix").setText(theme.getGreenMatrix()));
        themeToBeSaved.addContent(new Element("blue_matrix").setText(theme.getBlueMatrix()));
        themeToBeSaved.addContent(new Element("time").setText(theme.getTimeString()));
        savedThemes.addContent(themeToBeSaved);
        output(themesDocument, themesXmlFile);
    }

    public void updateThemeInXml(Theme oldTheme, Theme newTheme) {
        Element themeElement = getElementByTheme(oldTheme);
        themeElement.getChild("name").setText(newTheme.getName());
        themeElement.getChild("red_matrix").setText(newTheme.getRedMatrix());
        themeElement.getChild("green_matrix").setText(newTheme.getGreenMatrix());
        themeElement.getChild("blue_matrix").setText(newTheme.getBlueMatrix());
        themeElement.getChild("time").setText(newTheme.getTimeString());
        output(themesDocument, themesXmlFile);
    }

    public void updateGraphicsConfXml(Theme theme) {
        Element gUIColourElement = graphicsConfDocument.getRootElement().getChild("GUIColour").getChild("Default");
        gUIColourElement.getChild("MatrixRed").setText(theme.getRedMatrix());
        gUIColourElement.getChild("MatrixGreen").setText(theme.getGreenMatrix());
        gUIColourElement.getChild("MatrixBlue").setText(theme.getBlueMatrix());
        tryToOutputGraphicsConf();
    }

    private void tryToOutputGraphicsConf() {
        try {
            output(graphicsConfDocument, new File(propertyEditor.getKey("gconf")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteThemeInXml(Theme theme) {
        themesDocument.getRootElement().removeContent(getElementByTheme(theme));
        output(themesDocument, themesXmlFile);
    }

    private Element getElementByTheme(Theme theme) {
        for (Element element : themesDocument.getRootElement().getChildren()) {
            if (element.getChild("name").getText().equals(theme.getName()))
                return element;
        }
        return null;
    }

    public Theme[] getThemesFromXml() {
        List<Element> themeElements = themesDocument.getRootElement().getChildren();
        Theme[] themes = new Theme[themeElements.size()];
        for (int i = 0; i < themeElements.size(); i++) {
            try {
                Element themeElement =  themeElements.get(i);
                themes[i] = new Theme(themeElement.getChildText("name"), themeElement.getChildText("red_matrix"),
                       themeElement.getChildText("green_matrix"), themeElement.getChildText("blue_matrix"),
                        themeElement.getChildText("time"));
            } catch (ParseException | MatrixFormattingException e) {
                Util.ALERT("Fatal Themes Error", "Reading themes from file failed, please delete themes.xml!", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
        return themes;
    }

    private void output(Document doc, File file) {
        try (FileOutputStream fo = new FileOutputStream(file)){
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, fo);
        } catch (IOException e) {
            e.printStackTrace();
            Util.ALERT("Failed to save theme!", "An error occurred saving theme!", Alert.AlertType.ERROR);
        }
    }

    private Document documentBuilder(File file){
        try {
            if (!themesXmlFile.exists()) {
                tryToCreateFile();
            }
            return new SAXBuilder().build(file);
        } catch (JDOMException | IOException ignored) {}
        return new Document(new Element("themes"));
    }

    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }
}
