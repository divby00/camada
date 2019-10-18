package org.wildcat.camada.utils;

import javafx.collections.ObservableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvUtils {

    public static <T> void export(ObservableList<T> selectedItems, String[] csvHeader, String fileName) {
        boolean result = false;
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(csvHeader));
            for (T item : selectedItems) {
                String fields = StringUtils.substringBetween(item.toString(), "(", ")");
                List<String> data = Arrays.stream(fields.split(", "))
                        .map(s -> s.split("=")[1])
                        .collect(Collectors.toList());
                csvPrinter.printRecord(data);
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
