package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.InvalidPriceException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);
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
        log.info("StockController initialized with StockBasket.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing StockController UI components");
        refreshStockItems();
        this.warehouseBarCode.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                fillInputsBySelectedStockItem();
            }
        });
    }

    @FXML
    public void addProductClicked() {
        log.info("Add product button clicked.");
        try {
            String barCode = warehouseBarCode.getText();
            String name = warehouseName.getText();
            String priceText = warehousePrice.getText();
            String quantityText = warehouseAmount.getText();

            log.debug("Extracted product details - Barcode: {}, Name: {}, Price: {}, Quantity: {}", barCode, name, priceText, quantityText);

            Long id = stockBasket.generateId(barCode);
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            boolean success = stockBasket.addProductToStock(id, name, price, quantity);
            if (success) {
                log.info("Product successfully added to stock - ID: {}, Name: {}, Price: {}, Quantity: {}", id, name, price, quantity);
                resetProductFields();
                refreshStockItems();
            }
        } catch (InvalidPriceException e) {
            log.info("Invalid price for product");
            showError("Invalid Price", e.getMessage());
        } catch (SalesSystemException e) {
            log.info("Error adding product to stock");
            showError("Error", e.getMessage());
        } catch (NumberFormatException e) {
            log.info("Input error - Price and amount must be valid numbers. Entered Price: {}, Amount: {}", warehousePrice.getText(), warehouseAmount.getText());
            showError("Input Error", "Price and amount must be valid numbers.");
        }
    }

    private void fillInputsBySelectedStockItem() {
        log.debug("Attempting to fill inputs based on entered barcode: {}", warehouseBarCode.getText());
        StockItem stockItem = stockBasket.getStockItemByBarcode(warehouseBarCode.getText());
        if (stockItem != null) {
            warehouseName.setText(stockItem.getName());
            warehousePrice.setText(String.valueOf(stockItem.getPrice()));
            log.info("Stock item found - Name: {}, Price: {}", stockItem.getName(), stockItem.getPrice());
        } else {
            resetProductFields();
            log.warn("No stock item found with barcode: {}", warehouseBarCode.getText());
        }
    }

    private void resetProductFields() {
        warehouseBarCode.clear();
        warehouseName.clear();
        warehousePrice.clear();
        warehouseAmount.clear();
        log.debug("Product fields reset to default values.");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        log.info("Displayed error dialog - Title: {}, Message: {}", title, message);
    }

    @FXML
    public void refreshButtonClicked() {
        log.info("Refresh button clicked.");
        refreshStockItems();
    }

    private void refreshStockItems() {
        log.debug("Refreshing stock items in the warehouse table.");
        warehouseTableView.setItems(FXCollections.observableList(stockBasket.getAllStockItems()));
        warehouseTableView.refresh();
    }
}
