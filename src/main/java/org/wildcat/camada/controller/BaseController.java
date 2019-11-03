package org.wildcat.camada.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.entity.CustomQuery;
import org.wildcat.camada.service.CustomQueryService;
import org.wildcat.camada.utils.AlertUtils;
import org.wildcat.camada.utils.CsvUtils;
import org.wildcat.camada.utils.PdfUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public abstract class BaseController<T> implements Initializable {

    ObservableList<T> tableData;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button signoffButton;

    @FXML
    private ImageView imageBanner;

    @FXML
    private ComboBox<CustomQuery> customQueriesComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<T> table;

    @Resource
    private final CustomQueryService customQueryService;

    @Lazy
    @Autowired
    protected StageManager stageManager;

    private ResourceBundle resources;

    protected BaseController(CustomQueryService customQueryService) {
        this.customQueryService = customQueryService;
    }

    abstract void initTable();

    abstract void setTableItems(Task<ObservableList<T>> task);

    abstract List<T> findAllByCustomQuery(CustomQuery value);

    abstract void delete(T item);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Random random = new Random(System.currentTimeMillis());
        String name = "back0" + random.nextInt(3) + ".png";
        imageBanner.setImage(new Image(name, true));

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
                tableData = taskTableItems.getValue();
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
                    tableData = taskListListener.getValue();
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

    public void onSignoffButtonAction(ActionEvent event) {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    public void onHomeButtonAction(ActionEvent event) {
        stageManager.switchScene(FxmlView.HOME);
    }

    public void onExportCsvButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("*.csv");
        File file = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
        if (file != null) {
            CsvUtils.export(table, file.getAbsolutePath());
        }
    }

    public void onExportPdfButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("*.pdf");
        File file = fileChooser.showSaveDialog(this.stageManager.getPrimaryStage());
        if (file != null) {
            Task<Boolean> pdfTask = new Task<Boolean>() {
                @Override
                protected Boolean call() {
                    return PdfUtils.export(table, "Listado de usuarios", file.getAbsolutePath());
                }
            };
            progressIndicator.visibleProperty().bind(pdfTask.runningProperty());
            new Thread(pdfTask).start();
            pdfTask.setOnSucceeded(workerState -> {
                if (pdfTask.getValue()) {
                    AlertUtils.showInfo(MessageFormat.format("El fichero {0} se ha guardado correctamente.", file.getName()));
                } else {
                    AlertUtils.showError(MessageFormat.format("Ha habido un problema al guardar el fichero {0}.", file.getName()));
                }
            });
            pdfTask.setOnFailed(workerState -> {
                AlertUtils.showError(MessageFormat.format("Ha habido un problema al guardar el fichero {0}.", file.getName()));
            });
        }
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
