package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the History tab.
 */
public class HistoryController implements Initializable {

    private static final Logger log = LogManager.getLogger(HistoryController.class);
    private final SalesSystemDAO dao;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableView<Purchase> purchaseTable;
    @FXML
    private TableView<SoldItem> itemTable;

    // Injected Buttons
    @FXML
    private Button showAllButton;
    @FXML
    private Button showLast10Button;
    @FXML
    private Button showBetweenDatesButton;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing HistoryController");

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));

        initializePurchaseTable();
        initializeItemTable();
        refreshPurchaseTable();
    }

    private void initializePurchaseTable() {
        TableColumn<Purchase, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateTime().toLocalDate().toString()));

        TableColumn<Purchase, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateTime().toLocalTime().toString()));

        TableColumn<Purchase, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        purchaseTable.getColumns().setAll(dateColumn, timeColumn, totalColumn);
        purchaseTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        purchaseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPurchaseDetails(newValue)
        );
    }

    private void initializeItemTable() {
        TableColumn<SoldItem, Long> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(
                cellData.getValue().getStockItem().getId()).asObject());

        TableColumn<SoldItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SoldItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SoldItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SoldItem, Double> sumColumn = new TableColumn<>("Sum");
        sumColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getSum()).asObject());

        itemTable.getColumns().setAll(idColumn, nameColumn, priceColumn, quantityColumn, sumColumn);
    }

    private void refreshPurchaseTable() {
        List<Purchase> purchases = dao.getPurchaseHistory();

        if (purchases == null) {
            purchases = new ArrayList<>();
        }

        ObservableList<Purchase> observablePurchases = FXCollections.observableArrayList(purchases);
        purchaseTable.setItems(observablePurchases);
    }

    private void showPurchaseDetails(Purchase purchase) {
        if (purchase != null) {
            ObservableList<SoldItem> items = FXCollections.observableArrayList(purchase.getSoldItems());
            //System.out.println(items);
            itemTable.setItems(items);
        } else {
            itemTable.getItems().clear();
        }
    }

    /**
     * Event handler for the "Show all" button.
     */
    @FXML
    private void handleShowAll() {
        log.info("Show All button clicked");
        refreshPurchaseTable();
    }

    /**
     * Event handler for the "Show last 10" button.
     */
    @FXML
    private void handleShowLast10() {
        log.info("Show Last 10 button clicked");
        List<Purchase> allPurchases = dao.getPurchaseHistory();
        List<Purchase> last10Purchases = allPurchases.stream()
                .sorted(Comparator.comparing(Purchase::getDateTime).reversed())
                .limit(10)
                .collect(Collectors.toList());
        ObservableList<Purchase> purchases = FXCollections.observableArrayList(last10Purchases);
        purchaseTable.setItems(purchases);
    }

    /**
     * Event handler for the "Show between dates" button.
     */
    @FXML
    private void handleShowBetweenDates() {
        log.info("Show Between Dates button clicked");
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "Invalid Dates", "Please select both start and end dates.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Date Range", "End date cannot be before start date.");
            return;
        }

        List<Purchase> filteredPurchases = dao.getPurchaseHistory().stream()
                .filter(purchase -> {
                    LocalDate purchaseDate = purchase.getDateTime().toLocalDate();
                    return (purchaseDate.isEqual(startDate) || purchaseDate.isAfter(startDate)) &&
                            (purchaseDate.isEqual(endDate) || purchaseDate.isBefore(endDate));
                })
                .sorted(Comparator.comparing(Purchase::getDateTime).reversed())
                .collect(Collectors.toList());

        ObservableList<Purchase> purchases = FXCollections.observableArrayList(filteredPurchases);
        purchaseTable.setItems(purchases);
    }

    /**
     * Utility method to show alerts.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
