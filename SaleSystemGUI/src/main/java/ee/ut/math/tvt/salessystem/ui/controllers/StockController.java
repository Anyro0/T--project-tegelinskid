package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.logic.StockBasket;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;

import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private final StockBasket stockBasket;
    @FXML
    private TextField warehouseBarCode;

    @FXML
    private TextField warehouseAmount;

    @FXML
    private TextField warehouseName;

    @FXML
    private TextField warehousePrice;

    @FXML
    private Button addProduct;
    @FXML
    private TableView<StockItem> warehouseTableView;

    public StockController(SalesSystemDAO dao) {
        this.stockBasket = new StockBasket(dao);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        this.warehouseBarCode.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                fillInputsBySelectedStockItem();
            }
        });
    }

    @FXML
    public void addProductClicked() {
        try {
            String barCode = warehouseBarCode.getText();
            String name = warehouseName.getText();
            String priceText = warehousePrice.getText();
            String quantityText = warehouseAmount.getText();

            Long id = stockBasket.generateId(barCode);
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            if (price < 0) {
                showError("Invalid price", "Price can't be a negative number.");
                throw new SalesSystemException("Price cannot be a negative number.");
            } else if (quantity <= 0) {
                showError("Invalid amount", "Amount must be a positive number.");
                throw new SalesSystemException("Amount must be a positive number.");
            } else {
                boolean success = stockBasket.addProductToStock(id, name, price, quantity);
                if (success) {
                    resetProductFields();
                    refreshStockItems();
                } else {
                    showError("Duplicate Barcode", "Barcode must be unique.");
                    throw new SalesSystemException("Barcode must be unique. Duplicate barcode found.");
                }
            }
        } catch (NumberFormatException e) {
            showError("Input Error", "Price and amount must be valid numbers.");
            throw new SalesSystemException("Price and amount must be valid numbers.", e);
        }
    }

    private void fillInputsBySelectedStockItem() {
        StockItem stockItem = stockBasket.getStockItemByBarcode(warehouseBarCode.getText());
        if (stockItem != null) {
            warehouseName.setText(stockItem.getName());
            warehousePrice.setText(String.valueOf(stockItem.getPrice()));
        } else {
            resetProductFields();
        }
    }

    private void resetProductFields() {
        warehouseBarCode.clear();
        warehouseName.clear();
        warehousePrice.clear();
        warehouseAmount.clear();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(stockBasket.getAllStockItems()));
        warehouseTableView.refresh();
    }
}
