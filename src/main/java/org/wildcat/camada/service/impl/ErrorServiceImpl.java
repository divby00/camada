package org.wildcat.camada.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wildcat.camada.service.ErrorService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ErrorServiceImpl implements ErrorService {

    @Value("${logging.file.name}")
    private String logFile;

    @Override
    public String getErrorMessage(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHardwareInfo());
        sb.append(getExceptionTrace(throwable));
        sb.append(getLog());
        return sb.toString();
    }

    private String getHardwareInfo() {
        StringBuilder sb = new StringBuilder("* Hardware information *" + System.lineSeparator());
        sb.append("Available processors (cores): " + Runtime.getRuntime().availableProcessors() + System.lineSeparator());
        sb.append("Free memory (bytes): " + Runtime.getRuntime().freeMemory() + System.lineSeparator());

        long maxMemory = Runtime.getRuntime().maxMemory();
        sb.append("Maximum memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory) + System.lineSeparator());
        sb.append("Total memory available to JVM (bytes): " + Runtime.getRuntime().totalMemory() + System.lineSeparator());

        File[] roots = File.listRoots();
        for (File root : roots) {
            sb.append("File system root: " + root.getAbsolutePath() + System.lineSeparator());
            sb.append("Total space (bytes): " + root.getTotalSpace() + System.lineSeparator());
            sb.append("Free space (bytes): " + root.getFreeSpace() + System.lineSeparator());
            sb.append("Usable space (bytes): " + root.getUsableSpace() + System.lineSeparator());
        }

        sb.append("Java version: " + System.getProperty("java.version") + System.lineSeparator());

        return sb.toString() + System.lineSeparator();
    }

    private String getExceptionTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder("* Exception *" + System.lineSeparator());
        sb.append(ExceptionUtils.getStackTrace(throwable));
        return sb.toString() + System.lineSeparator();
    }

    private String getLog() {
        String result = StringUtils.EMPTY;
        try {
            StringBuilder sb = new StringBuilder("* Log file *" + System.lineSeparator());
            Path path = Paths.get("", logFile).toAbsolutePath();
            sb.append(Files.readString(path));
            result = sb.toString() + System.lineSeparator();
        } catch (Exception exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
        }
        return result;
    }
}
