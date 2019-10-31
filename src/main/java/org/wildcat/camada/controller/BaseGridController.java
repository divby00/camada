package org.wildcat.camada.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.wildcat.camada.entity.CustomQuery;
import org.wildcat.camada.service.CustomQueryService;
import org.wildcat.camada.utils.AlertUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public abstract class BaseGridController<T> implements Initializable {

    ObservableList<T> tableData;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ComboBox<CustomQuery> customQueriesComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<T> table;

    @Resource
    private final CustomQueryService customQueryService;

    private ResourceBundle resources;

    protected BaseGridController(CustomQueryService customQueryService) {
        this.customQueryService = customQueryService;
    }

    abstract void initTable();

    abstract void setTableItems(Task<ObservableList<T>> task);

    abstract List<T> findAllByCustomQuery(CustomQuery value);

    abstract void delete(T item);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        Task<ObservableList<CustomQuery>> taskCustomQueries = new Task<ObservableList<CustomQuery>>() {
            @Override
            protected ObservableList<CustomQuery> call() {
                List<CustomQuery> queries = customQueryService.findAllBySection(FxmlView.USER);
                return FXCollections.observableList(queries);
            }
        };
        progressIndicator.visibleProperty().bind(taskCustomQueries.runningProperty());
        new Thread(taskCustomQueries).start();

        taskCustomQueries.setOnSucceeded(worker -> {
            ObservableList<CustomQuery> queries = taskCustomQueries.getValue();
            customQueriesComboBox.setItems(queries);
            initCustomQueriesCombo(customQueriesComboBox);

            Task<ObservableList<T>> taskTableItems = getObservableListTask(customQueriesComboBox.getValue());
            progressIndicator.visibleProperty().bind(taskTableItems.runningProperty());
            new Thread(taskTableItems).start();
            taskTableItems.setOnSucceeded(workerState -> {
                //tableData = taskTableItems.getValue();
                setTableItems(taskTableItems);
            });
            taskTableItems.setOnCancelled(workerState -> {
                AlertUtils.showError("Ha ocurrido un error al obtener la lista de items.");
            });
            taskTableItems.setOnFailed(workerState -> {
                AlertUtils.showError("Ha ocurrido un error al obtener la lista de items.");
            });

            customQueriesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                Task<ObservableList<T>> taskListListener = getObservableListTask(newVal);
                progressIndicator.visibleProperty().bind(taskListListener.runningProperty());
                new Thread(taskListListener).start();
                taskListListener.setOnSucceeded(workerState -> {
                    //tableData = taskTableItems.getValue();
                    setTableItems(taskListListener);
                    searchTextField.setText(StringUtils.EMPTY);
                });
                taskListListener.setOnCancelled(workerState -> {
                    AlertUtils.showError("Ha ocurrido un error al obtener la lista de items.");
                });
                taskListListener.setOnFailed(workerState -> {
                    AlertUtils.showError("Ha ocurrido un error al obtener la lista de items.");
                });
            });
        });
        taskCustomQueries.setOnFailed(worker -> {
            AlertUtils.showError("Ha ocurrido un error al obtener la lista de consultas.");
        });
        taskCustomQueries.setOnCancelled(worker -> {
            AlertUtils.showError("Ha ocurrido un error al obtener la lista de consultas.");
        });
        initTable();
        addContextMenu();
    }

    private void initCustomQueriesCombo(ComboBox<CustomQuery> customQueriesComboBox) {
        Callback<ListView<CustomQuery>, ListCell<CustomQuery>> comboCellFactory = getComboCellFactory();
        customQueriesComboBox.setCellFactory(comboCellFactory);
        customQueriesComboBox.setButtonCell(comboCellFactory.call(null));
        customQueriesComboBox.getSelectionModel().selectFirst();
    }

    private Callback<ListView<CustomQuery>, ListCell<CustomQuery>> getComboCellFactory() {
        return new Callback<ListView<CustomQuery>, ListCell<CustomQuery>>() {
            @Override
            public ListCell<CustomQuery> call(ListView<CustomQuery> l) {
                return new ListCell<CustomQuery>() {
                    @Override
                    protected void updateItem(CustomQuery item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getDescription());
                        }
                    }
                };
            }
        };
    }

    private Task<ObservableList<T>> getObservableListTask(CustomQuery value) {
        return new Task<ObservableList<T>>() {
            @Override
            protected ObservableList<T> call() {
                List<T> allByCustomQuery = findAllByCustomQuery(value);
                return FXCollections.observableList(allByCustomQuery);
            }
        };
    }

    private void addContextMenu() {
        table.setRowFactory(tableView -> {
            final TableRow<T> row = new TableRow<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };

            final ContextMenu rowMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Borrar");
            removeItem.setOnAction(event -> {
                ButtonType buttonType = AlertUtils.showConfirmation("¿Quieres borrar el registro?");
                if (buttonType == ButtonType.YES) {
                    T item = row.getItem();
                    Task<Boolean> deleteRegisterTask = new Task<Boolean>() {
                        @Override
                        protected Boolean call() {
                            boolean result = false;
                            try {
                                delete(item);
                                tableData.remove(item);
                                table.refresh();
                                result = true;
                            } catch (Exception ignored) {
                            }
                            return result;
                        }
                    };
                    progressIndicator.visibleProperty().bind(deleteRegisterTask.runningProperty());
                    new Thread(deleteRegisterTask).start();
                    deleteRegisterTask.setOnSucceeded(worker -> {
                        if (deleteRegisterTask.getValue()) {
                            AlertUtils.showInfo("El registro se ha borrado correctamente.");
                        } else {
                            AlertUtils.showInfo("Ha ocurrido un problema al borrar el registro.");
                        }
                    });
                    deleteRegisterTask.setOnCancelled(worker -> {
                        AlertUtils.showInfo("Ha ocurrido un problema al borrar el registro.");
                    });
                    deleteRegisterTask.setOnFailed(worker -> {
                        AlertUtils.showInfo("Ha ocurrido un problema al borrar el registro.");
                    });
                }
            });
            rowMenu.getItems().addAll(removeItem);
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu).otherwise((ContextMenu) null));
            return row;
        });
    }


}
