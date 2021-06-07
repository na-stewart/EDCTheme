package main.java.com.sunsetdev.EDCTheme.util;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class Util {
    public static void ALERT(String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, content);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void OPEN_BROWSER(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try{
            if (os.contains("win"))
                rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
            else if (os.contains("mac"))
                rt.exec( "open " + url);
            else if (os.contains("nix") || os.contains("nux")) {
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape","opera","links","lynx","chrome"};
                StringBuilder cmd = new StringBuilder();
                for (int i=0; i<browsers.length; i++)
                    cmd.append(i == 0 ? "" : " || ").append(browsers[i]).append(" \"").append(url).append("\" ");
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
