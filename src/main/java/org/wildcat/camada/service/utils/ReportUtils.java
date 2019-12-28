package org.wildcat.camada.service.utils;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
class ReportUtils {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}.+$");

    static <T> String[] getVisibleColumns(TableView<T> table) {
        List<String> visibleColumns = table.getColumns().stream()
                .filter(TableColumnBase::isVisible)
                .map(TableColumnBase::getText)
                .collect(Collectors.toList());
        String[] headers = new String[visibleColumns.size()];
        return visibleColumns.toArray(headers);
    }

    static <T> List getRecordList(TableView<T> table, T item) {
        return table.getVisibleLeafColumns().stream()
                .map(column -> column.getCellObservableValue(item).getValue())
                .map(ReportUtils::getValue)
                .collect(Collectors.toList());
    }

    static <T> Map<String, List<?>> getRecordsMap(TableView<T> table) {
        Map<String, List<?>> data = new TreeMap<>();
        for (int i = 0; i < table.getItems().size(); ++i) {
            T item = table.getItems().get(i);
            List<?> row = table.getVisibleLeafColumns().stream()
                    .map(column -> column.getCellObservableValue(item).getValue())
                    .map(ReportUtils::getValue)
                    .collect(Collectors.toList());
            data.put(Integer.toString(i), row);
        }
        return data;
    }

    /**
     * Format some values before exporting them to CSV or PDF.
     */
    private static Object getValue(Object column) {
        String value = String.valueOf(column);
        if (StringUtils.containsAny(value, "true", "false")) {
            return ("true".equals(value)) ? "Si" : "No";
        } else if (DATE_PATTERN.matcher(value).matches()) {
            try {
                Date date = DateUtils.parseDate(StringUtils.substringBefore(value, " "), "yyyy-MM-dd");
                return DateFormatUtils.format(date, "dd/MM/yyyy");
            } catch (ParseException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
            }
        }
        return column;
    }
}
