package org.wildcat.camada.service.utils;

import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.FileWriter;
import java.text.MessageFormat;

@Slf4j
public class CsvUtils {

    public static <T> void export(TableView<T> table, String fileName) {
        boolean result = false;
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(ReportUtils.getVisibleColumns(table)));
            for (T item : table.getItems()) {
                csvPrinter.printRecord(ReportUtils.getRecordList(table, item));
            }
            fileWriter.flush();
            fileWriter.close();
            csvPrinter.close();
            result = true;
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        if (result) {
            AlertUtils.showInfo(MessageFormat.format("El fichero {0} se ha guardado correctamente.", fileName));
        } else {
            AlertUtils.showError(MessageFormat.format("Ha habido un problema al guardar el fichero {0}.", fileName));
        }
    }

}
