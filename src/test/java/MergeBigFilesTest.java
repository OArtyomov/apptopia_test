import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.commons.io.FileUtils.lineIterator;

public class MergeBigFilesTest {

    @Test
    public void testMergeBigFiles() throws IOException, ParseException {
        String fileName1 = "C:\\Temp\\TestMergeBigFiles\\fileA";
        String fileName2 = "C:\\Temp\\TestMergeBigFiles\\fileB";
        boolean endOfFirstFile = false;
        boolean endOfSecondFile = false;
        List<LineAsObject> resultList = Lists.newArrayList();
        try (LineIterator it1 = lineIterator(new File(fileName1), "UTF-8")) {
            try (LineIterator it2 = lineIterator(new File(fileName2), "UTF-8")) {
                LineAsObject firstObject = null;
                LineAsObject secondObject = null;
                boolean firstShouldNext = true;
                boolean secondShouldNext = true;
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
                    if (objectWithMinValue != null) {
                        resultList.add(objectWithMinValue);
                    }

                    if (objectWithMinValue == firstObject) {
                        firstShouldNext = true;
                        secondShouldNext = false;
                    }
                    if (objectWithMinValue == secondObject) {
                        firstShouldNext = false;
                        secondShouldNext = true;
                    }

                }
            }
        }
        resultList.stream().forEach(item -> System.out.println(item.getDate() + ":" + item.getValue()));

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

    private LineAsObject convertLineToObject(String line) throws ParseException {
        String[] splitData = line.split(":");
        LineAsObject result = new LineAsObject();
        Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(splitData[0]);
        Integer value = Integer.valueOf(splitData[1]);
        result.setDate(parse);
        result.setValue(value);
        return result;
    }
}
