package org.wildcat.camada.utils;

import javafx.scene.control.TableView;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.text.MessageFormat;

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
        } catch (Exception ignored) {
        }
        if (result) {
            AlertUtils.showInfo(MessageFormat.format("El fichero {0} se ha guardado correctamente.", fileName));
        } else {
            AlertUtils.showError(MessageFormat.format("Ha habido un problema al guardar el fichero {0}.", fileName));
        }
    }

}
