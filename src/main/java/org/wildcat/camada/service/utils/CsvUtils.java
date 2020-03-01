package org.wildcat.camada.service.utils;

import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wildcat.camada.common.validator.Validator;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class CsvUtils {

    public static Boolean excelCompatible;
    public static Character csvDelimiter;

    @Value("${excel.compatible}")
    public void setExcelCompatible(String value) {
        excelCompatible = Boolean.valueOf(value);
    }

    @Value("${csv.delimiter}")
    public void setCsvDelimiter(Character value) {
        csvDelimiter = value;
    }

    public static <T> void export(TableView<T> table, String fileName) {
        boolean result = false;
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            if (excelCompatible) { // Add a BOM for better Excel support
                fileWriter.write("\uFEFF");
            }
            CSVFormat format = (excelCompatible)
                    ? CSVFormat.EXCEL.withDelimiter(csvDelimiter).withHeader(ReportUtils.getVisibleColumns(table))
                    : CSVFormat.DEFAULT.withDelimiter(csvDelimiter).withHeader(ReportUtils.getVisibleColumns(table));

            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, format);
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

    public static void export(String[] headers, List<String[]> list, String fileName) {
        boolean result = false;
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            if (excelCompatible) { // Add a BOM for better Excel support
                fileWriter.write("\uFEFF");
            }
            CSVFormat format = (excelCompatible)
                    ? CSVFormat.EXCEL.withDelimiter(csvDelimiter).withHeader(headers)
                    : CSVFormat.DEFAULT.withDelimiter(csvDelimiter).withHeader(headers);

            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, format);
            for (String[] items : list) {
                csvPrinter.printRecord((Object[]) items);
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

    public static CSVParser getCsvParser(File file) {
        CSVParser csvParser = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
            CSVFormat format = (excelCompatible)
                    ? CSVFormat.EXCEL.withDelimiter(csvDelimiter)
                    : CSVFormat.DEFAULT.withDelimiter(csvDelimiter);
            csvParser = CSVParser.parse(reader, format
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        return csvParser;
    }

    public static Pair<Boolean, List<String>> validateFile(File file, CsvFileDefinitions csvFileDefinitions) {
        boolean result = false;
        List<String> errors = new ArrayList<>();
        try {
            List<Validator> validators = csvFileDefinitions.getValidators();
            CSVParser csvParser = CsvUtils.getCsvParser(file);
            for (CSVRecord csvRecord : csvParser) {
                List<String> fields = csvFileDefinitions.getFields();
                for (int i = 0; i < fields.size(); ++i) {
                    String field = csvRecord.get(i);
                    Validator validator = validators.get(i);
                    if (!validator.validate(field)) {
                        errors.add(
                                "El campo [ " + field + " ] de la columna [" + fields.get(i) + "] en el registro número ["
                                        + csvRecord.getRecordNumber() + "] es incorrecto: " + validator.getErrorMessage());
                    }
                }
            }
            csvParser.close();
            result = errors.size() == 0;
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        return Pair.of(result, errors);
    }

    public static boolean checkDuplicatedEntriesInCsv(File file) {
        List<String> entities = new LinkedList<>();
        for (CSVRecord csvRecord : CsvUtils.getCsvParser(file)) {
            entities.add(csvRecord.get(0));
        }
        Set<String> entitiesSet = new HashSet<>(entities);
        return entitiesSet.size() == entities.size() && entities.size() != 0;
    }

    public static boolean getBooleanFromTranslatedValue(String value) {
        boolean result = Boolean.FALSE;
        if (StringUtils.equalsIgnoreCase(value, "si")) {
            result = Boolean.TRUE;
        }
        return result;
    }

    public static Date getDateFromTranslatedValue(String value) throws ParseException {
        Date result = null;
        if (value != null) {
            result = DateUtils.parseDate(value, "dd/MM/yyyy");
        }
        return result;
    }
}
