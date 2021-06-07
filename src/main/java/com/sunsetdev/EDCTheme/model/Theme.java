package main.java.com.sunsetdev.EDCTheme.model;

import main.java.com.sunsetdev.EDCTheme.model.exception.MatrixFormattingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Theme implements Cloneable {
    private String name;
    private String redMatrix;
    private String greenMatrix;
    private String blueMatrix;
    private String timeString;
    private LocalTime localTime;

    public Theme(String name, String redMatrix, String greenMatrix, String blueMatrix, String timeString) throws ParseException, MatrixFormattingException {
        this.name = name;
        this.redMatrix = checkedMatrix(redMatrix);
        this.greenMatrix = checkedMatrix(greenMatrix);
        this.blueMatrix = checkedMatrix(blueMatrix);
        this.timeString = timeString;
        if (!timeString.isEmpty())
            this.localTime = checkedLocalTime(timeString);
    }

    public Theme(String name, String redMatrix, String greenMatrix, String blueMatrix, LocalTime localTime) throws MatrixFormattingException {
        this.name = name;
        this.redMatrix = checkedMatrix(redMatrix);
        this.greenMatrix = checkedMatrix(greenMatrix);
        this.blueMatrix = checkedMatrix(blueMatrix);
        this.localTime = localTime;
        this.timeString = localTime.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedMatrix() {
        return redMatrix;
    }

    public void setRedMatrix(String redMatrix) throws MatrixFormattingException {
        this.redMatrix = checkedMatrix(redMatrix);
    }

    public String getGreenMatrix() {
        return greenMatrix;
    }

    public void setGreenMatrix(String greenMatrix) throws MatrixFormattingException {
        this.greenMatrix = checkedMatrix(greenMatrix);
    }

    public String getBlueMatrix() {
        return blueMatrix;
    }

    public void setBlueMatrix(String blueMatrix) throws MatrixFormattingException {
        this.blueMatrix = checkedMatrix(blueMatrix);
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public void setTimeWithString(String timeString) throws ParseException {
        if (!timeString.isEmpty()) {
            this.localTime = checkedLocalTime(timeString);
            this.timeString = timeString;
        }
    }
    
    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    private String checkedMatrix(String matrix) throws MatrixFormattingException {
        try {
            List<String> mList = new ArrayList<>(Arrays.asList(matrix.split(",")));
            if (mList.size() > 3)
                throw new MatrixFormattingException(matrix + " matrix cannot have more than three values! Must be in form 0,0,0!");
            for (String s : mList) Double.parseDouble(s);
            return matrix;
        } catch (NumberFormatException e){
            throw new MatrixFormattingException(matrix + " matrix is not correctly formatted! Must be in form 0,0,0!");
        }
    }

    private LocalTime checkedLocalTime(String time) throws ParseException {
        LocalTime parsedTime;
        try {
            String timeToUpper = time.toUpperCase();
            if (timeToUpper.contains("AM") || timeToUpper.contains("PM")){
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                Date date = parseFormat.parse(time);
                parsedTime = LocalTime.parse(displayFormat.format(date));
            }
            else {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("k:mm");
                parsedTime = LocalTime.parse(time, df);
            }
        } catch (DateTimeParseException e) {
            throw new ParseException(e.getMessage(), e.getErrorIndex());
        }
        return parsedTime;
    }

    @Override
    public String toString() {
        return name;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
