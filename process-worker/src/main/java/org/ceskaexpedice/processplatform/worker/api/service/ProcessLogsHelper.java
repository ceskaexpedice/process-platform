package org.ceskaexpedice.processplatform.worker.api.service;

import org.ceskaexpedice.processplatform.common.ApplicationException;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ProcessLogsHelper
 */
public class ProcessLogsHelper {

    private static Logger LOGGER = Logger.getLogger(ProcessLogsHelper.class.getName());

    private final File logFile;

    public ProcessLogsHelper(File logFile) {
        this.logFile = logFile;
    }

    public long getLogFileSize() {
        RandomAccessFile errorProcessRAFile = null;
        try {
            errorProcessRAFile = new RandomAccessFile(logFile, "r");

            return errorProcessRAFile.length();
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, ex.getMessage(), ex);
            return 0;
        } finally {
            try {
                if (errorProcessRAFile != null) errorProcessRAFile.close();
            } catch (IOException e) {
                throw new ApplicationException(e.getMessage(), e);
            }
        }
    }

    public List<String> getLogFileData(long offset, long limit) {
        String asString = getLogFileDataAsString(offset, limit);
        String[] splitByNewLine = asString.split("\\r?\\n");
        List<String> result = Arrays.asList(splitByNewLine);
        return result;
    }

    private String getLogFileDataAsString(long offset, long limit) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(logFile, "r");
            return readFromRAF(raf, offset, limit);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
            return "";
        } catch (IOException e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
            return "";
        } finally {
            try {
                if (raf != null) raf.close();
            } catch (IOException e) {
                LOGGER.log(Level.FINE, e.getMessage(), e);
            }
        }
    }

    private String readFromRAF(RandomAccessFile raf, long offset, long limit) throws IOException {
        //ignore data before new line unless it is the very beginning of the file
        //we don't want to start in the middle of the line, so we rather return less then limit by cutting the beginning
        if (offset != 0) {
            raf.seek(offset);
            String remainsOfPreviousLine = raf.readLine();
            offset = offset + remainsOfPreviousLine.length();
            limit = limit + remainsOfPreviousLine.length();
        }
        byte[] buffer = new byte[(int) limit];
        raf.seek(offset);
        int read = raf.read(buffer);
        if (read >= 0) {
            byte[] nbuffer = new byte[read];
            System.arraycopy(buffer, 0, nbuffer, 0, read);
            String dataRead = new String(nbuffer);

            //read to the end of line if there's still some data,
            //we don't want to break lines, so we rather return slightly more then limit by adding data until new line
            long newOffset = offset + limit;
            if (newOffset < raf.length()) {
                raf.seek(newOffset);
                String remainsOfCurrentLine = raf.readLine();
                return dataRead + remainsOfCurrentLine;
            } else {
                return dataRead;
            }
        } else {
            return "";
        }
    }

}
