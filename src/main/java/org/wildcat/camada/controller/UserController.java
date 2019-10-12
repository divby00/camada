package org.wildcat.camada.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.utils.AlertUtils;
import org.wildcat.camada.utils.FilterUtils;
import org.wildcat.camada.utils.TableUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class UserController implements Initializable {

    @FXML
    private TableView<CamadaUser> listTable;

    @FXML
    private TableColumn<CamadaUser, String> userName;

    @FXML
    private TableColumn<CamadaUser, String> name;

    @FXML
    private TableColumn<CamadaUser, String> surname;

    @FXML
    private TableColumn<CamadaUser, String> email;

    @FXML
    private TableColumn<CamadaUser, Boolean> isAdmin;

    @FXML
    private TableColumn<CamadaUser, Boolean> isVirtualSponsor;

    @FXML
    private TableColumn<CamadaUser, Boolean> isPresentialSponsor;

    @FXML
    private TableColumn<CamadaUser, Boolean> isPartner;

    @FXML
    private TableColumn<CamadaUser, Boolean> isVolunteer;

    @FXML
    private TableColumn<CamadaUser, Date> activationDate;

    @FXML
    private TableColumn<CamadaUser, Date> lastConnection;

    @FXML
    private TextField filterUserName;

    @FXML
    private TextField filterName;

    @FXML
    private TextField filterSurname;

    @FXML
    private TextField filterEmail;

    @FXML
    private CheckBox filterAdmin;

    @FXML
    private CheckBox filterPartner;

    @FXML
    private CheckBox filterVolunteer;

    @FXML
    private CheckBox filterVirtualSponsor;

    @FXML
    private CheckBox filterPresentialSponsor;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    public UserController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    private void prepareTableColumn(TableColumn<CamadaUser, String> column, String columnName) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareTableColumn(userName, "name");
        userName.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            CamadaUser user = event.getRowValue();
            String oldValue = user.getFirstName();
            if (newValue.equals(oldValue))
                return;
            ButtonType buttonType = AlertUtils.showUpdateAlert(oldValue, newValue);
            if (buttonType == ButtonType.YES) {
                user.setName(newValue);
                event.getRowValue().setName(newValue);
                camadaUserService.save(user);
            } else {
                user.setName(oldValue);
                event.getRowValue().setName(oldValue);
                listTable.refresh();
            }
        });

        prepareTableColumn(name, "firstName");
        name.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            CamadaUser user = event.getRowValue();
            String oldValue = user.getFirstName();
            if (newValue.equals(oldValue))
                return;
            ButtonType buttonType = AlertUtils.showUpdateAlert(oldValue, newValue);
            if (buttonType == ButtonType.YES) {
                user.setFirstName(newValue);
                event.getRowValue().setFirstName(newValue);
                camadaUserService.save(user);
            } else {
                user.setFirstName(oldValue);
                event.getRowValue().setFirstName(oldValue);
                listTable.refresh();
            }
        });

        prepareTableColumn(surname, "lastName");
        surname.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            CamadaUser user = event.getRowValue();
            String oldValue = user.getLastName();
            if (newValue.equals(oldValue))
                return;
            ButtonType buttonType = AlertUtils.showUpdateAlert(oldValue, newValue);
            if (buttonType == ButtonType.YES) {
                user.setLastName(newValue);
                event.getRowValue().setLastName(newValue);
                camadaUserService.save(user);
            } else {
                user.setLastName(oldValue);
                event.getRowValue().setLastName(oldValue);
                listTable.refresh();
            }
        });

        prepareTableColumn(email, "email");
        email.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            CamadaUser user = event.getRowValue();
            String oldValue = user.getEmail();
            if (newValue.equals(oldValue))
                return;
            ButtonType buttonType = AlertUtils.showUpdateAlert(oldValue, newValue);
            if (buttonType == ButtonType.YES) {
                user.setEmail(newValue);
                event.getRowValue().setEmail(newValue);
                camadaUserService.save(user);
            } else {
                user.setEmail(oldValue);
                event.getRowValue().setEmail(oldValue);
                listTable.refresh();
            }
        });

        isAdmin.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIsAdmin()));
        isAdmin.setCellFactory(p -> getBooleanTableCell());

        isVirtualSponsor.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIsVirtualSponsor()));
        isVirtualSponsor.setCellFactory(p -> getBooleanTableCell());

        isPresentialSponsor.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIsPresentialSponsor()));
        isPresentialSponsor.setCellFactory(p -> getBooleanTableCell());

        isPartner.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIsPartner()));
        isPartner.setCellFactory(p -> getBooleanTableCell());

        isVolunteer.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIsVolunteer()));
        isVolunteer.setCellFactory(p -> getBooleanTableCell());

        activationDate.setCellValueFactory(new PropertyValueFactory<>("activationDate"));
        lastConnection.setCellValueFactory(new PropertyValueFactory<>("lastConnection"));

        activationDate.setCellFactory(column -> TableUtils.getDateTableCell("dd/MM/yyyy"));
        lastConnection.setCellFactory(column -> TableUtils.getDateTableCell("dd/MM/yyyy HH:mm:ss"));

        // Populate table
        Iterable<CamadaUser> camadaUsers = camadaUserService.findAll();
        ObservableList<CamadaUser> iterables = FXCollections.observableList((List<CamadaUser>) camadaUsers);
        listTable.setItems(iterables);

        // Add context menu for updating and deleting rows
        listTable.setRowFactory(tableView -> {
            final TableRow<CamadaUser> row = new TableRow<CamadaUser>() {
                @Override
                protected void updateItem(CamadaUser user, boolean empty) {
                    super.updateItem(user, empty);
                }
            };

            // Add context menu to the row
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Borrar usuario");
            removeItem.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Quieres borrar el usuario?'", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    CamadaUser user = row.getItem();
                    camadaUserService.delete(user);
                    listTable.getItems().remove(user);
                }
            });
            rowMenu.getItems().addAll(removeItem);

            // only display context menu for non-null items:
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu) null));
            return row;
        });

    }

    private TableCell<CamadaUser, Boolean> getBooleanTableCell() {
        CheckBox checkBox = new CheckBox();
        TableCell<CamadaUser, Boolean> tableCell = new TableCell<CamadaUser, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) return;
                setGraphic(checkBox);
                checkBox.setSelected(item);
            }
        };
        checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (validate(checkBox, event)) {
                CamadaUser item = (CamadaUser) tableCell.getTableRow().getItem();
                camadaUserService.save(item);
                listTable.refresh();
            }
        });
        checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
              if (validate(checkBox, event)) {
                  CamadaUser item = (CamadaUser) tableCell.getTableRow().getItem();
                  camadaUserService.save(item);
                  listTable.refresh();
              }
            }
        });

        tableCell.setAlignment(Pos.CENTER);
        tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return tableCell;
    }

    public void filterChange(ActionEvent event) {
    }

    public void search(ActionEvent event) {
        Map<String, Object> filterMap = buildFilterMap();
        String query = FilterUtils.buildQuery(filterMap);
    }

    public void goToHome(ActionEvent event) {
        stageManager.switchScene(FxmlView.DASHBOARD);
    }

    private Map<String, Object> buildFilterMap() {
        Map<String, Object> map = new HashMap<>();
        FilterUtils.mapField(map, "name", filterUserName.getText());
        FilterUtils.mapField(map, "first_name", filterName.getText());
        FilterUtils.mapField(map, "last_name", filterSurname.getText());
        FilterUtils.mapField(map, "email", filterEmail.getText());
        FilterUtils.mapField(map, "is_admin", filterAdmin.isSelected());
        FilterUtils.mapField(map, "is_partner", filterPartner.isSelected());
        FilterUtils.mapField(map, "is_virtual_sponsor", filterVirtualSponsor.isSelected());
        FilterUtils.mapField(map, "is_presential_sponsor", filterPresentialSponsor.isSelected());
        FilterUtils.mapField(map, "is_volunteer", filterVolunteer.isSelected());
        return map;
    }

    private boolean validate(CheckBox checkBox, Event event) {
        boolean result = false;
        event.consume();
        ButtonType buttonType = AlertUtils.showUpdateAlert(Boolean.toString(checkBox.isSelected()), Boolean.toString(!checkBox.isSelected()));
        if (buttonType == ButtonType.YES) {
            checkBox.setSelected(!checkBox.isSelected());
            result = true;
        }
        return result;
    }

}
