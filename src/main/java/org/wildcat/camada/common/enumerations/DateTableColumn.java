package org.wildcat.camada.common.enumerations;

import jfxtras.scene.control.CalendarTextField;
import org.wildcat.camada.service.PersistenceService;

public interface DateTableColumn {
    <T> void setDate(T item, CalendarTextField datePicker, PersistenceService persistenceService);
}
