package frc.team1836.robot.util.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Writes data to a CSV file
 */
public class ReflectingCSVWriter<T> {

    ConcurrentLinkedDeque<String> mLinesToWrite = new ConcurrentLinkedDeque<>();
    PrintWriter mOutput = null;
    Field[] mFields;

    public ReflectingCSVWriter(String fileName, Class<T> typeClass) {
        mFields = typeClass.getFields();
        try {
            String dateStamp = new SimpleDateFormat(" hh-mm-ssaaa").format(new Date());
            File fi = new File("/u/" + fileName + dateStamp + ".csv");
            mOutput = new PrintWriter(fi);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Write field names.
        StringBuffer line = new StringBuffer();
        for (Field field : mFields) {
            if (line.length() != 0) {
                line.append(", ");
            }
            line.append(field.getName());
        }
        writeLine(line.toString());
    }

    public void add(T value) {
        StringBuffer line = new StringBuffer();
        for (Field field : mFields) {
            if (line.length() != 0) {
                line.append(", ");
            }
            try {
                line.append(field.get(value).toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mLinesToWrite.add(line.toString());
    }

    protected synchronized void writeLine(String line) {
        if (mOutput != null) {
            mOutput.println(line);
        }
    }

    // Call this periodically from any thread to write to disk.
    public void write() {
        while (true) {
            String val = mLinesToWrite.pollFirst();
            if (val == null) {
                break;
            }
            writeLine(val);
        }
    }

    public synchronized void flush() {
        if (mOutput != null) {
            write();
            mOutput.flush();
        }
    }
}
