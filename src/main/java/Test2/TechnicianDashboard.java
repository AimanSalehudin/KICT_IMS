package Test2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.format.DateTimeFormatter;

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
        addButton.setOnAction(e -> FileUtil.addMaintenanceRecord(
                (Stage) addButton.getScene().getWindow(),
                maintenanceRecords,
                inventoryItems
        ));

        updateButton.setOnAction(e -> {
            MaintenanceRecord selected = maintenanceTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                FileUtil.updateMaintenanceRecord(
                        (Stage) updateButton.getScene().getWindow(),
                        selected,
                        maintenanceRecords
                );
            } else {
                showAlert("No Selection", "Please select a record to update");
            }
        });

        deleteButton.setOnAction(e -> {
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
        });

        inventoryButton.setOnAction(e -> showInventoryView());
        logoutButton.setOnAction(e -> logout());
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
}