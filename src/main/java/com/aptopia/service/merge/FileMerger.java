package com.aptopia.service.merge;


import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.io.FileUtils.lineIterator;

public class FileMerger {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public void mergeFiles(String fileName1, String fileName2, String resultFileName) throws IOException, ParseException {
        boolean endOfFirstFile = false;
        boolean endOfSecondFile = false;
        String encoding = "UTF-8";
        File file = new File(resultFileName);
        if (file.exists()) {
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding))) {
            try (LineIterator it1 = lineIterator(new File(fileName1), encoding)) {
                try (LineIterator it2 = lineIterator(new File(fileName2), encoding)) {
                    LineAsObject firstObject = null;
                    LineAsObject secondObject = null;
                    LineAsObject currentObject = null;
                    boolean firstShouldNext = true;
                    boolean secondShouldNext = true;
                    boolean fistLineInOutputFile = true;
                    while (!endOfFirstFile || !endOfSecondFile) {
                        if (firstShouldNext) {
                            if (it1.hasNext()) {
                                String line = it1.nextLine();
                                firstObject = convertLineToObject(line);
                            } else {
                                endOfFirstFile = true;
                                firstObject = null;
                            }
                        }

                        if (secondShouldNext) {
                            if (it2.hasNext()) {
                                String line = it2.nextLine();
                                secondObject = convertLineToObject(line);
                            } else {
                                endOfSecondFile = true;
                                secondObject = null;
                            }
                        }
                        LineAsObject objectWithMinValue = calculateObjectWithMinValue(firstObject, secondObject);
                        if (currentObject == null) {
                            currentObject = objectWithMinValue;
                        }
                        if (objectWithMinValue != null) {
                            if (currentObject.getDate().equals(objectWithMinValue.getDate())) {
                                currentObject.setValue(currentObject.getValue() + objectWithMinValue.getValue());
                            } else {
                                fistLineInOutputFile = addLineToFile(writer, currentObject, fistLineInOutputFile);
                                currentObject = objectWithMinValue;
                            }
                        }
                        firstShouldNext = (objectWithMinValue == firstObject);
                        secondShouldNext = (objectWithMinValue == secondObject);
                    }
                    if (currentObject != null) {
                        addLineToFile(writer, currentObject, fistLineInOutputFile);
                    }
                }
            }
        }

    }

    private boolean addLineToFile(BufferedWriter writer, LineAsObject currentObject, boolean firstLineInOutputFile) throws IOException {
        String newLine = convertLinObjectToString(currentObject);
        if (!firstLineInOutputFile) {
            writer.newLine();
        }
        writer.write(newLine);
        return false;
    }

    private LineAsObject calculateObjectWithMinValue(LineAsObject firstObject, LineAsObject secondObject) {
        if (firstObject == null) {
            return secondObject;
        }
        if (secondObject == null) {
            return firstObject;
        }
        Date firstDate = firstObject.getDate();
        Date secondDate = secondObject.getDate();
        if (firstDate.before(secondDate)) {
            return firstObject;
        }
        return secondObject;
    }

    private String convertLinObjectToString(LineAsObject object) {
        return new SimpleDateFormat(DATE_FORMAT).format(object.getDate()) + ":" + object.getValue();
    }

    private LineAsObject convertLineToObject(String line) throws ParseException {
        String[] splitData = line.split(":");
        LineAsObject result = new LineAsObject();
        Date parse = new SimpleDateFormat(DATE_FORMAT).parse(splitData[0]);
        Integer value = Integer.valueOf(splitData[1]);
        result.setDate(parse);
        result.setValue(value);
        return result;
    }
}
