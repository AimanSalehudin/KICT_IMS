package Test2.javafx.view;

import Test2.Java.model.*;
import Test2.Java.util.FileUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TechnicianDashboard {
    private final User currentUser;
    private final ObservableList<MaintenanceRecord> maintenanceRecords;
    private final ObservableList<InventoryItem> inventoryItems;

    private final TableView<MaintenanceRecord> maintenanceTable = new TableView<>();
    private final Button addButton = new Button("Add Record");
    private final Button updateButton = new Button("Update Record");
    private final Button deleteButton = new Button("Delete Record");
    private final Button inventoryButton = new Button("View Inventory");
    private final Button logoutButton = new Button("Logout");

    public TechnicianDashboard(User user) {
        this.currentUser = user;
        this.maintenanceRecords = FXCollections.observableArrayList(FileUtil.loadMaintenance());
        this.inventoryItems = FXCollections.observableArrayList(FileUtil.loadInventory());
        initializeUIComponents();
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Technician Dashboard - KICT Inventory System");
        primaryStage.setScene(createMainScene());
        primaryStage.show();
    }

    private void initializeUIComponents() {
        configureMaintenanceTable();
        styleButtons();
        setupButtonActions();
    }

    private void configureMaintenanceTable() {
        maintenanceTable.setItems(maintenanceRecords);

        // Maintenance record columns
        TableColumn<MaintenanceRecord, String> itemIdCol = new TableColumn<>("Item ID");
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));

        TableColumn<MaintenanceRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<MaintenanceRecord, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("maintenanceType"));

        TableColumn<MaintenanceRecord, String> remarksCol = new TableColumn<>("Remarks");
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        TableColumn<MaintenanceRecord, Void> actionCol = createActionColumn();

        maintenanceTable.getColumns().add(itemIdCol);
        maintenanceTable.getColumns().add(dateCol);
        maintenanceTable.getColumns().add(typeCol);
        maintenanceTable.getColumns().add(remarksCol);
        maintenanceTable.getColumns().add(actionCol);

        maintenanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private TableColumn<MaintenanceRecord, Void> createActionColumn() {
        TableColumn<MaintenanceRecord, Void> column = new TableColumn<>("Actions");
        column.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");

            {
                detailsButton.setOnAction(event -> {
                    MaintenanceRecord record = getTableView().getItems().get(getIndex());
                    showRecordDetails(record);
                });
                detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailsButton);
            }
        });
        return column;
    }

    private void styleButtons() {
        String buttonStyle = "-fx-font-weight: bold; -fx-padding: 8 15; -fx-text-fill: white; ";
        addButton.setStyle(buttonStyle + "-fx-background-color: #4CAF50;");
        updateButton.setStyle(buttonStyle + "-fx-background-color: #2196F3;");
        deleteButton.setStyle(buttonStyle + "-fx-background-color: #f44336;");
        inventoryButton.setStyle(buttonStyle + "-fx-background-color: #9C27B0;");
        logoutButton.setStyle(buttonStyle + "-fx-background-color: #FF9800;");
    }

    private void setupButtonActions() {
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                addMaintenanceRecord(
                    (Stage) addButton.getScene().getWindow(),
                    maintenanceRecords,
                    inventoryItems);
            }
        });

        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                MaintenanceRecord selected = maintenanceTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    updateMaintenanceRecord(
                            (Stage) updateButton.getScene().getWindow(),
                            selected,
                            maintenanceRecords
                    );
                } else {
                    showAlert("No Selection", "Please select a record to update");
                }
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                MaintenanceRecord selected = maintenanceTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (showConfirmation("Delete Record",
                            "Are you sure you want to delete this maintenance record?")) {
                        maintenanceRecords.remove(selected);
                        FileUtil.saveMaintenance(maintenanceRecords);
                    }
                } else {
                    showAlert("No Selection", "Please select a record to delete");
                }
            }
        });

        inventoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showInventoryView();
            }
        });
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                logout();
            }
        });
    }

    private Scene createMainScene() {
        Label welcomeLabel = new Label("Welcome, " + currentUser.getName() + getTechnicianSpecialization());
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, inventoryButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, welcomeLabel, maintenanceTable, buttonBox, logoutButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        return new Scene(root, 900, 600);
    }

    private String getTechnicianSpecialization() {
        return currentUser instanceof TechnicianUser ?
                " (" + ((TechnicianUser) currentUser).getSpecialization() + ")" : "";
    }

    private void showRecordDetails(MaintenanceRecord record) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Maintenance Record Details");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Item ID:"), new Label(record.getItemId()));
        grid.addRow(1, new Label("Date:"), new Label(record.getDate().toString()));
        grid.addRow(2, new Label("Type:"), new Label(record.getMaintenanceType()));

        TextArea remarksArea = new TextArea(record.getRemarks());
        remarksArea.setEditable(false);
        remarksArea.setWrapText(true);
        remarksArea.setPrefRowCount(3);
        grid.addRow(3, new Label("Remarks:"), remarksArea);

        if (record instanceof RepairMaintenance) {
            grid.addRow(4, new Label("Severity:"),
                    new Label(((RepairMaintenance) record).getSeverity()));
        } else if (record instanceof InspectionMaintenance) {
            grid.addRow(4, new Label("Inspector:"),
                    new Label(((InspectionMaintenance) record).getInspector()));
        }

        Scene scene = new Scene(grid, 400, 250);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void showInventoryView() {
        Stage inventoryStage = new Stage();
        inventoryStage.setTitle("Inventory View");

        TableView<InventoryItem> inventoryTable = new TableView<>(inventoryItems);

        // Proper inventory item columns
        TableColumn<InventoryItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<InventoryItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<InventoryItem, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        inventoryTable.getColumns().add(idCol);
        inventoryTable.getColumns().add(nameCol);
        inventoryTable.getColumns().add(categoryCol);
        inventoryTable.getColumns().add(qtyCol);

        VBox box = new VBox(inventoryTable);
        box.setPadding(new Insets(10));

        inventoryStage.setScene(new Scene(box, 500, 400));
        inventoryStage.show();
    }

    private void logout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        new LoginScreen().start(new Stage());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }
    // Add Maintenance Record
    public static void addMaintenanceRecord(Stage stage, ObservableList<MaintenanceRecord> records,
                                            ObservableList<InventoryItem> inventoryItems) {
        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        ComboBox<String> itemComboBox = new ComboBox<>();
        for (InventoryItem item : inventoryItems) {
            itemComboBox.getItems().add(item.getId());
        }

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Routine", "Repair", "Inspection", "General");
        typeCombo.setValue("General");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea remarks = new TextArea();
        remarks.setPrefRowCount(3);

        // Type-specific fields
        GridPane specificFields = new GridPane();
        specificFields.setVgap(5);
        specificFields.setHgap(5);

        // Show/hide fields based on type selection
        typeCombo.setOnAction(e -> updateMaintenanceFields(typeCombo.getValue(), specificFields));

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemComboBox);
        pane.addRow(1, new Label("Type:"), typeCombo);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(specificFields, 0, 4, 2, 1);
        pane.add(btnBox, 1, 5);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                MaintenanceRecord record = createMaintenanceRecord(
                        itemComboBox.getValue(),
                        typeCombo.getValue(),
                        datePicker.getValue(),
                        remarks.getText(),
                        specificFields
                );

                if (record != null) {
                    records.add(record);
                    FileUtil.saveMaintenance(records);
                    form.close();
                }
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        Scene scene = new Scene(pane, 600, 400);
        form.setScene(scene);
        form.setTitle("Add Maintenance Record");
        form.showAndWait();
    }

    private static void updateMaintenanceFields(String type, GridPane pane) {
        pane.getChildren().clear();

        switch (type) {
            case "Repair":
                TextField severity = new TextField();
                pane.addRow(0, new Label("Severity:"), severity);
                break;
            case "Inspection":
                TextField inspector = new TextField();
                pane.addRow(0, new Label("Inspector:"), inspector);
                break;
        }
    }

    private static MaintenanceRecord createMaintenanceRecord(String itemId, String type,
                                                             LocalDate date, String remarks, GridPane specificFields) {
        switch (type) {
            case "Routine":
                return new RoutineMaintenance(itemId, date, remarks);
            case "Repair":
                RepairMaintenance repair = new RepairMaintenance(itemId, date, remarks);
                TextField severity = (TextField) specificFields.getChildren().get(1);
                repair.setSeverity(severity.getText());
                return repair;
            case "Inspection":
                InspectionMaintenance inspection = new InspectionMaintenance(itemId, date, remarks);
                TextField inspector = (TextField) specificFields.getChildren().get(1);
                inspection.setInspector(inspector.getText());
                return inspection;
            default:
                return new GeneralMaintenance(itemId, date, remarks);
        }
    }

    // Update Maintenance record
    public static void updateMaintenanceRecord(Stage stage, MaintenanceRecord record,
                                               ObservableList<MaintenanceRecord> list) {
        if (record == null) return;

        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        TextField itemIdField = new TextField(record.getItemId());
        itemIdField.setEditable(false);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Routine", "Repair", "Inspection", "General");
        typeCombo.setValue(record.getMaintenanceType());

        DatePicker datePicker = new DatePicker(record.getDate());
        TextArea remarks = new TextArea(record.getRemarks());
        remarks.setPrefRowCount(3);

        // Type-specific fields
        GridPane specificFields = new GridPane();
        specificFields.setVgap(5);
        specificFields.setHgap(5);

        // Initialize fields based on record type
        if (record instanceof RepairMaintenance) {
            RepairMaintenance repair = (RepairMaintenance) record;
            TextField severity = new TextField(repair.getSeverity());
            specificFields.addRow(0, new Label("Severity:"), severity);
        } else if (record instanceof InspectionMaintenance) {
            InspectionMaintenance inspection = (InspectionMaintenance) record;
            TextField inspector = new TextField(inspection.getInspector());
            specificFields.addRow(0, new Label("Inspector:"), inspector);
        }

        // Update fields when type changes
        typeCombo.setOnAction(e -> updateMaintenanceFields(typeCombo.getValue(), specificFields));

        Button save = new Button("Update");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Item ID:"), itemIdField);
        pane.addRow(1, new Label("Type:"), typeCombo);
        pane.addRow(2, new Label("Date:"), datePicker);
        pane.addRow(3, new Label("Remarks:"), remarks);
        pane.add(specificFields, 0, 4, 2, 1);
        pane.add(btnBox, 1, 5);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                list.remove(record);
                MaintenanceRecord updatedRecord = createMaintenanceRecord(
                        itemIdField.getText(),
                        typeCombo.getValue(),
                        datePicker.getValue(),
                        remarks.getText(),
                        specificFields
                );

                if (updatedRecord != null) {
                    list.add(updatedRecord);
                    FileUtil.saveMaintenance(list);
                    form.close();
                }
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                form.close();
            }
        });

        Scene scene = new Scene(pane, 600, 400);
        form.setScene(scene);
        form.setTitle("Update Maintenance Record");
        form.showAndWait();
    }
}