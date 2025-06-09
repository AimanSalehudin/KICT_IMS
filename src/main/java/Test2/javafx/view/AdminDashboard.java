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
        column.setCellFactory(new Callback<TableColumn<InventoryItem, String>, TableCell<InventoryItem, String>>() {
            @Override
            public TableCell<InventoryItem, String> call(TableColumn<InventoryItem, String> param) {
                return new TableCell<InventoryItem, String>() {
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
                };
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
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                addInventoryItem(
                        (Stage) addButton.getScene().getWindow(),
                        items
                );
            }
        });

        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                InventoryItem selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    updateInventoryItem(
                            (Stage) updateButton.getScene().getWindow(),
                            selected,
                            items
                    );
                } else {
                    showAlert("No Selection", "Please select an item to update");
                }
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                InventoryItem selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (showConfirmation("Delete Item", "Are you sure you want to delete " + selected.getName() + "?")) {
                        items.remove(selected);
                        FileUtil.saveInventory(items);
                    }
                } else {
                    showAlert("No Selection", "Please select an item to delete");
                }
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();
                new LoginScreen().start(new Stage());
            }
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
    // Add Inventory Item
    public static void addInventoryItem(Stage stage, ObservableList<InventoryItem> list) {
        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        // Common fields
        TextField id = new TextField();
        TextField name = new TextField();
        TextArea desc = new TextArea();
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, 1);
        desc.setPrefRowCount(3);

        // Item type selection
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("General", "Electrical", "Furniture");
        typeCombo.setValue("General");

        // Type-specific fields
        GridPane electricalFields = createElectricalFields();
        GridPane furnitureFields = createFurnitureFields();

        // Show/hide fields based on type selection
        typeCombo.setOnAction(e -> {
            electricalFields.setVisible("Electrical".equals(typeCombo.getValue()));
            furnitureFields.setVisible("Furniture".equals(typeCombo.getValue()));
        });

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("Type:"), typeCombo);
        pane.addRow(1, new Label("ID:"), id);
        pane.addRow(2, new Label("Name:"), name);
        pane.addRow(3, new Label("Description:"), desc);
        pane.addRow(4, new Label("Quantity:"), quantity);
        pane.add(electricalFields, 0, 5, 2, 1);
        pane.add(furnitureFields, 0, 5, 2, 1);
        pane.add(btnBox, 1, 6);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                InventoryItem item = createItemBasedOnType(
                        typeCombo.getValue(),
                        id.getText(),
                        name.getText(),
                        desc.getText(),
                        quantity.getValue(),
                        electricalFields,
                        furnitureFields
                );

                if (item != null) {
                    list.add(item);
                    FileUtil.saveInventory(list);
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
        form.setTitle("Add Inventory Item");
        form.showAndWait();
    }

    private static GridPane createElectricalFields() {
        GridPane pane = new GridPane();
        TextField voltage = new TextField();
        TextField powerSource = new TextField();
        pane.addRow(0, new Label("Voltage:"), voltage);
        pane.addRow(1, new Label("Power Source:"), powerSource);
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setVisible(false);
        return pane;
    }

    private static GridPane createFurnitureFields() {
        GridPane pane = new GridPane();
        TextField material = new TextField();
        TextField dimensions = new TextField();
        pane.addRow(0, new Label("Material:"), material);
        pane.addRow(1, new Label("Dimensions:"), dimensions);
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setVisible(false);
        return pane;
    }

    private static InventoryItem createItemBasedOnType(String type, String id, String name,
                                                       String desc, int quantity, GridPane electricalFields, GridPane furnitureFields) {
        switch (type) {
            case "Electrical":
                ElectricalItem elecItem = new ElectricalItem(id, name, desc, quantity);
                TextField voltage = (TextField) electricalFields.getChildren().get(1);
                TextField powerSource = (TextField) electricalFields.getChildren().get(3);
                elecItem.setVoltage(voltage.getText());
                elecItem.setPowerSource(powerSource.getText());
                return elecItem;
            case "Furniture":
                FurnitureItem furnItem = new FurnitureItem(id, name, desc, quantity);
                TextField material = (TextField) furnitureFields.getChildren().get(1);
                TextField dimensions = (TextField) furnitureFields.getChildren().get(3);
                furnItem.setMaterial(material.getText());
                furnItem.setDimensions(dimensions.getText());
                return furnItem;
            default:
                return new GeneralItem(id, name, desc, quantity);
        }
    }

    // Update Inventory Item
    public static void updateInventoryItem(Stage stage, InventoryItem item, ObservableList<InventoryItem> list) {
        if (item == null) return;

        Stage form = new Stage();
        form.initOwner(stage);
        GridPane pane = new GridPane();

        // Common fields
        TextField id = new TextField(item.getId());
        TextField name = new TextField(item.getName());
        TextArea desc = new TextArea(item.getDescription());
        Spinner<Integer> quantity = new Spinner<>(0, Integer.MAX_VALUE, item.getQuantity());
        desc.setPrefRowCount(3);

        // Type-specific fields
        GridPane specificFields = new GridPane();
        if (item instanceof ElectricalItem) {
            ElectricalItem elecItem = (ElectricalItem) item;
            TextField voltage = new TextField(elecItem.getVoltage());
            TextField powerSource = new TextField(elecItem.getPowerSource());
            specificFields.addRow(0, new Label("Voltage:"), voltage);
            specificFields.addRow(1, new Label("Power Source:"), powerSource);
        } else if (item instanceof FurnitureItem) {
            FurnitureItem furnItem = (FurnitureItem) item;
            TextField material = new TextField(furnItem.getMaterial());
            TextField dimensions = new TextField(furnItem.getDimensions());
            specificFields.addRow(0, new Label("Material:"), material);
            specificFields.addRow(1, new Label("Dimensions:"), dimensions);
        }

        Button save = new Button("Update");
        Button cancel = new Button("Cancel");

        HBox btnBox = new HBox(10, save, cancel);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));
        pane.addRow(0, new Label("ID:"), id);
        pane.addRow(1, new Label("Name:"), name);
        pane.addRow(2, new Label("Description:"), desc);
        pane.addRow(3, new Label("Quantity:"), quantity);
        pane.add(specificFields, 0, 4, 2, 1);
        pane.add(btnBox, 1, 5);

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                list.remove(item);
                InventoryItem updatedItem = createUpdatedItem(item, id.getText(), name.getText(),
                        desc.getText(), quantity.getValue(), specificFields);

                if (updatedItem != null) {
                    list.add(updatedItem);
                    FileUtil.saveInventory(list);
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
        form.setTitle("Update Inventory Item");
        form.showAndWait();
    }

    private static InventoryItem createUpdatedItem(InventoryItem original, String id, String name,
                                                   String desc, int quantity, GridPane specificFields) {
        if (original instanceof ElectricalItem) {
            ElectricalItem item = new ElectricalItem(id, name, desc, quantity);
            TextField voltage = (TextField) specificFields.getChildren().get(1);
            TextField powerSource = (TextField) specificFields.getChildren().get(3);
            item.setVoltage(voltage.getText());
            item.setPowerSource(powerSource.getText());
            return item;
        } else if (original instanceof FurnitureItem) {
            FurnitureItem item = new FurnitureItem(id, name, desc, quantity);
            TextField material = (TextField) specificFields.getChildren().get(1);
            TextField dimensions = (TextField) specificFields.getChildren().get(3);
            item.setMaterial(material.getText());
            item.setDimensions(dimensions.getText());
            return item;
        } else {
            return new GeneralItem(id, name, desc, quantity);
        }
    }
}
