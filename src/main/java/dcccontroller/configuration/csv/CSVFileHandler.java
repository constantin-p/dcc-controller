package dcccontroller.configuration.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CSVFileHandler {
    // https://github.com/constantin-p/dat16j-exam-project/blob/master/src/examproject/db/CSVFileHandler.java
    private static final char SEPARATOR_CHAR = ',';
    private static final char WRAPPER_CHAR = '"';

    public static void writeLine(Writer writer, List<String> values) throws IOException {
        boolean isFirstValue = true;

        for (String value : values) {
            // Write the value separator
            if (!isFirstValue) {
                writer.write(String.valueOf(SEPARATOR_CHAR));
            }

            // Write the wrapper char (START)
            writer.write(String.valueOf(WRAPPER_CHAR));

            // Write the value
            for (int i = 0; i < value.length(); i++) {
                char currentChar = value.charAt(i);
                if (currentChar == WRAPPER_CHAR) {
                    // Handle chars used as wrapper chars inside the value
                    writer.write(String.valueOf(WRAPPER_CHAR));
                }
                writer.write(currentChar);
            }

            // Write the wrapper char (END)
            writer.write(String.valueOf(WRAPPER_CHAR));
            isFirstValue = false;
        }

        writer.write("\n");
    }

    public static List<String> parseLine(Reader reader) throws IOException {
        int currentChar = reader.read();

        if (currentChar < 0) {
            return null;
        }

        ArrayList<String> row = new ArrayList<>();
        StringBuffer currentValue = new StringBuffer();
        boolean isInsideWrapperChar = false;
        boolean isInsideValueString = false;
        while (currentChar > 0) {
            // Parsing logic
            //      ignore: start & end wrapper chars
            //      append: wrapper chars inside the value, normal chars
            //      end:    no need to check for newline chars, Files.lines()
            //              doesn't return line terminator chars within the string lines
            if (isInsideWrapperChar) {
                isInsideValueString = true;
                if (currentChar == WRAPPER_CHAR) {
                    isInsideWrapperChar = false;
                } else {
                    currentValue.append((char) currentChar);
                }
            } else {
                if (currentChar == WRAPPER_CHAR) {
                    isInsideWrapperChar = true;
                    // Append wrapper chars that are inside the value
                    if (isInsideValueString) {
                        currentValue.append(WRAPPER_CHAR);
                    }
                } else if (currentChar == SEPARATOR_CHAR) {
                    // Add the current value to the row array and prepare to
                    // register the new value
                    row.add(currentValue.toString());
                    currentValue = new StringBuffer();
                    isInsideValueString = false;
                }
            }
            // Read the next char
            currentChar = reader.read();
        }
        // Add the last value to the row array
        row.add(currentValue.toString());
        return row;
    }
}
