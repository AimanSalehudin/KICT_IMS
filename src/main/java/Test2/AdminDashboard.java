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

public class AdminDashboard {
    private final User currentUser;
    private final ObservableList<InventoryItem> items;

    private final TableView<InventoryItem> tableView = new TableView<>();
    private final Button addButton = new Button("Add Item");
    private final Button updateButton = new Button("Update Item");
    private final Button deleteButton = new Button("Delete Item");
    private final Button logoutButton = new Button("Logout");

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.items = FXCollections.observableArrayList(FileUtil.loadInventory());
        initializeUIComponents();
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Dashboard - KICT Inventory System");
        primaryStage.setScene(createMainScene());
        primaryStage.show();
    }

    private void initializeUIComponents() {
        configureTableView();
        styleButtons();
        setupButtonActions();
    }

    private void configureTableView() {
        tableView.setItems(items);

        // Add columns one by one to avoid addAll issues
        tableView.getColumns().add(createTableColumn("ID", "id", 100));
        tableView.getColumns().add(createTableColumn("Name", "name", 150));
        tableView.getColumns().add(createTableColumn("Description", "description", 250));
        tableView.getColumns().add(createTableColumn("Quantity", "quantity", 80));
        tableView.getColumns().add(createTableColumn("Category", "category", 100));
        tableView.getColumns().add(createStatusColumn());

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private TableColumn<InventoryItem, String> createTableColumn(String title, String property, double width) {
        TableColumn<InventoryItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<InventoryItem, String> createStatusColumn() {
        TableColumn<InventoryItem, String> column = new TableColumn<>("Status");
        column.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    InventoryItem inventoryItem = getTableView().getItems().get(getIndex());
                    if (inventoryItem.needsMaintenance()) {
                        setText("NEEDS MAINTENANCE");
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setText("OK");
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                }
            }
        });
        return column;
    }

    private void styleButtons() {
        String buttonStyle = "-fx-font-weight: bold; -fx-padding: 8 15; -fx-text-fill: white; ";
        addButton.setStyle(buttonStyle + "-fx-background-color: #2a9df4;");
        updateButton.setStyle(buttonStyle + "-fx-background-color: #4CAF50;");
        deleteButton.setStyle(buttonStyle + "-fx-background-color: #f44336;");
        logoutButton.setStyle(buttonStyle + "-fx-background-color: #FF9800;");
    }

    private void setupButtonActions() {
        addButton.setOnAction(e -> FileUtil.addInventoryItem(
                (Stage) addButton.getScene().getWindow(),
                items
        ));

        updateButton.setOnAction(e -> {
            InventoryItem selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                FileUtil.updateInventoryItem(
                        (Stage) updateButton.getScene().getWindow(),
                        selected,
                        items
                );
            } else {
                showAlert("No Selection", "Please select an item to update");
            }
        });

        deleteButton.setOnAction(e -> {
            InventoryItem selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (showConfirmation("Delete Item",
                        "Are you sure you want to delete " + selected.getName() + "?")) {
                    items.remove(selected);
                    FileUtil.saveInventory(items);
                }
            } else {
                showAlert("No Selection", "Please select an item to delete");
            }
        });

        logoutButton.setOnAction(e -> {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            new LoginScreen().start(new Stage());
        });
    }

    private Scene createMainScene() {
        Label welcomeLabel = new Label("Welcome, " + currentUser.getName() + " (Admin)");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, welcomeLabel, tableView, buttonBox, logoutButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        return new Scene(root, 1000, 600);
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