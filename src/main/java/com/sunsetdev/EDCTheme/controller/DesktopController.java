package main.java.com.sunsetdev.EDCTheme.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.sunsetdev.EDCTheme.model.Theme;
import main.java.com.sunsetdev.EDCTheme.model.exception.MatrixFormattingException;
import main.java.com.sunsetdev.EDCTheme.util.Util;
import main.java.com.sunsetdev.EDCTheme.xml.EdctXMLEditor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DesktopController implements Initializable {
    @FXML
    private TextField themeName, redMatrix, greenMatrix, blueMatrix, graphicsConfPath, time;
    @FXML
    private ChoiceBox<Theme> themeChoiceBox;
    @FXML
    private CheckBox onTime;
    @FXML
    private Button setThemeButton;
    private EdctXMLEditor edctXMLEditor = new EdctXMLEditor();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tryToGetConfigurationFromProperties();
        setThemeButton.setDisable(onTime.isSelected());
        populateChoiceBox();
        initThemeChoiceBoxListener();
        initOnTimeListener();
        initThemeTimeListener();
    }


    private void tryToGetConfigurationFromProperties(){
        try {
            graphicsConfPath.setText(edctXMLEditor.getPropertyEditor().getKey("gconf"));
            onTime.setSelected(Boolean.parseBoolean(edctXMLEditor.getPropertyEditor().getKey("on_time")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOnTimeListener(){
        onTime.selectedProperty().addListener((observable, oldValue, newValue) -> {
            edctXMLEditor.getPropertyEditor().saveToConf("on_time", newValue.toString());
            setThemeButton.setDisable(onTime.isSelected());
            if (onTime.isSelected())
                setToClosestTheme();
        });
    }

    private void initThemeTimeListener(){
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        Runnable frameGrabber = (()-> Platform.runLater(this::setToClosestTheme));
        timer.scheduleAtFixedRate(frameGrabber, 0, 1, TimeUnit.MINUTES);
    }

    private void setToClosestTheme() {
        Theme closest = closestTheme();
        if (closest != null && onTime.isSelected()) {
            themeChoiceBox.setValue(closest);
            setTheme();
        }
    }


    private void initThemeChoiceBoxListener(){
        themeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                themeName.setText(newValue.getName());
                redMatrix.setText(newValue.getRedMatrix());
                blueMatrix.setText(newValue.getBlueMatrix());
                greenMatrix.setText(newValue.getGreenMatrix());
                time.setText(newValue.getTimeString());
            } catch (NullPointerException ignore){}
        });
    }


    @FXML
    private void openRedditLink(){
        Util.OPEN_BROWSER("https://www.reddit.com/r/EliteDangerous/comments/2p3784/you_can_manually_customize_the_gui_colors/");
    }

    @FXML
    private void openCockpitEditorLink(){
        Util.OPEN_BROWSER("http://arkku.com/elite/hud_editor/");
    }

    @FXML
    private void openAuthorLink(){
        Util.OPEN_BROWSER("https://github.com/sunset-developer");
    }


    @FXML
    private void add(){
        try {
            Theme createdTheme = new Theme(themeName.getText(), redMatrix.getText(), blueMatrix.getText(),
                    greenMatrix.getText(), time.getText());
            edctXMLEditor.addThemeToXML(createdTheme);
            themeChoiceBox.getItems().add(createdTheme);
            sortThemeChoiceBox();
            themeChoiceBox.setValue(createdTheme);
            clear();
        } catch (ParseException  | MatrixFormattingException e) {
            e.printStackTrace();
            Util.ALERT("Incorrect formatting!", "A field was incorrectly formatted! Please reference from examples! " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void update(){
        try {
            Theme selectedTheme = themeChoiceBox.getValue();
            Theme oldTheme = (Theme) selectedTheme.clone();
            selectedTheme.setName(themeName.getText());
            selectedTheme.setRedMatrix(redMatrix.getText());
            selectedTheme.setBlueMatrix(blueMatrix.getText());
            selectedTheme.setGreenMatrix(greenMatrix.getText());
            selectedTheme.setTimeWithString(time.getText());
            edctXMLEditor.updateThemeInXml(oldTheme, selectedTheme);
            sortThemeChoiceBox();
            themeChoiceBox.setValue(selectedTheme);
        } catch (ParseException | MatrixFormattingException e) {
            e.printStackTrace();
            Util.ALERT("Incorrect formatting!", "A field was incorrectly formatted! Please reference examples! " + e.getMessage() , Alert.AlertType.ERROR);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void delete(){
        Theme selectedTheme = themeChoiceBox.getSelectionModel().getSelectedItem();
        edctXMLEditor.deleteThemeInXml(selectedTheme);
        themeChoiceBox.getItems().remove(selectedTheme);
        clear();
    }

    private void populateChoiceBox() {
        for (Theme theme : edctXMLEditor.getThemesFromXml())
            themeChoiceBox.getItems().add(theme);
        sortThemeChoiceBox();
    }


    @FXML
    private void clear(){
        themeName.clear();
        redMatrix.clear();
        greenMatrix.clear();
        blueMatrix.clear();
        time.clear();
    }


    @FXML
    private void importGraphicsConf(){
        File conf = selectedFile();
        edctXMLEditor.getPropertyEditor().saveToConf("gconf", conf.getAbsolutePath());
        edctXMLEditor.importGraphicsConf();
        graphicsConfPath.setText(conf.getAbsolutePath());
    }

    @FXML
    private void setTheme(){
        try {
            edctXMLEditor.updateGraphicsConfXml(themeChoiceBox.getValue());
        } catch (NullPointerException e){
            e.printStackTrace();
            Util.ALERT("Set Theme Error",
                    "Please import the Graphics Configuraton using the \"Import\" button and select a theme!", Alert.AlertType.ERROR);
        }
    }

    private File selectedFile() {
        FileChooser f = new FileChooser();
        return f.showOpenDialog(new Stage());
    }

    private Theme closestTheme(){
        Theme closest = null;
        for (Theme theme :  themeChoiceBox.getItems()) {
            if (theme.getLocalTime() != null && LocalTime.now().isAfter(theme.getLocalTime()))
                closest = theme;
        }
        return closest;
    }

    private void sortThemeChoiceBox(){
        Collections.sort(themeChoiceBox.getItems(), (o1, o2) -> {
            if (o1.getLocalTime() == null || o2.getLocalTime() == null)  {
                return -1;
            } else
                return Double.compare(o1.getLocalTime().toNanoOfDay(), o2.getLocalTime().toNanoOfDay());
        });
    }
}
