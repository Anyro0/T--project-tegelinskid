package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private final SalesSystemDAO dao;
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
    private Button addItem;
    @FXML
    private TableView<StockItem> warehouseTableView;

    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        // TODO refresh view after adding new items
    }
    @FXML
    public void addProductClicked() {
        String barCode = warehouseBarCode.getText();
        String name = warehouseName.getText();
        String priceText = warehousePrice.getText();
        String quantityText = warehouseAmount.getText();

        Long id = Long.parseLong(barCode);
        double price = Double.parseDouble(priceText);
        int quantity = Integer.parseInt(quantityText);

        StockItem newItem = new StockItem(id, name, "", price, quantity);

        dao.saveStockItem(newItem);
        refreshStockItems();
        warehouseBarCode.clear();
        warehouseName.clear();
        warehousePrice.clear();
        warehouseAmount.clear();

    }
    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }
}
