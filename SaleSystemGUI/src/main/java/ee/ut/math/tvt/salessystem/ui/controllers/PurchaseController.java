package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "Point-of-sale" in the menu). Consists of the purchase menu,
 * current purchase dialog and shopping cart table.
 */
public class PurchaseController implements Initializable {

    private static final Logger log = LogManager.getLogger(PurchaseController.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    @FXML
    private Button newPurchase;
    @FXML
    private Button submitPurchase;
    @FXML
    private Button cancelPurchase;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private ChoiceBox<String> nameField; //NameChoiceBox
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;

    private List<String> choiceBoxItems;

    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing PurchaseController");
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        ObservableList items = FXCollections.observableList(
                shoppingCart.getAll() != null ? shoppingCart.getAll() : new ArrayList<>()
        );
        purchaseTableView.setItems(items);

        disableProductField(true);

        choiceBoxItems = dao.findStockItemsNames();
        if (choiceBoxItems != null) {
            nameField.getItems().addAll(choiceBoxItems);
        } else {
            log.warn("No items found for choice box.");
        }
        nameField.setOnAction(this::nameFieldChanged);

        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                }
            }
        });
        log.debug("PurchaseController initialized with DAO and ShoppingCart references");
    }

    private void nameFieldChanged(ActionEvent actionEvent) {
        String newName = nameField.getValue();
        log.debug("Name field changed to: {}", newName);
        StockItem stockItemWithTheSameName;
        if (dao.findStockItem(newName) == null) {
            barCodeField.setText("");
            priceField.setText("");
            log.info("No stock item found with name: {}", "Empty String (\"\")");
        } else {
            stockItemWithTheSameName = dao.findStockItem(newName);
            barCodeField.setText(Long.toString(stockItemWithTheSameName.getId()));
            priceField.setText(Double.toString(stockItemWithTheSameName.getPrice()));
            log.debug("Stock item found - ID: {}, Price: {}", stockItemWithTheSameName.getId(), stockItemWithTheSameName.getPrice());
        }
    }


    /**
     * Event handler for the <code>new purchase</code> event.
     */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("New sale process started");
        try {
            enableInputs();

            //ChoiceBOX refresh
            choiceBoxItems = dao.findStockItemsNames();
            if (choiceBoxItems != null) {
                nameField.getItems().setAll(choiceBoxItems);
            } else {
                log.warn("No items found for choice box.");
            }

        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Sale cancelled");
        try {
            shoppingCart.cancelCurrentPurchase();
            disableInputs();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.info("Couldn't cancel sale");
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete");
        try {
            List<SoldItem> items = new ArrayList<>(shoppingCart.getAll());
            Purchase purchase = new Purchase(LocalDateTime.now(), items);
            //dao.savePurchase(purchase);
            shoppingCart.submitCurrentPurchase();
            disableInputs();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
        log.info("dao Stock items" + dao.findStockItems());
    }

    // switch UI to the state that allows to proceed with the purchase
    private void enableInputs() {
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
        log.debug("Inputs enabled for a new purchase");
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        resetProductField();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        newPurchase.setDisable(false);
        disableProductField(true);
        log.debug("Inputs disabled after sale submission or cancellation");
    }

    private void fillInputsBySelectedStockItem() {
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            nameField.setValue(stockItem.getName());
            priceField.setText(String.valueOf(stockItem.getPrice()));
            log.debug("Filled inputs with stock item - Name: {}, Price: {}", stockItem.getName(), stockItem.getPrice());
        } else {
            resetProductField();
            log.warn("No stock item found with the entered barcode");
        }
    }

    // Search the warehouse for a StockItem with the bar code entered
    // to the barCode textfield.
    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            log.debug("Searching for stock item with barcode: {}", code);
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            log.warn("Invalid barcode entered: {}", barCodeField.getText());
            return null;
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        // add chosen item to the shopping cart.
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                shoppingCart.addItem(new SoldItem(stockItem, quantity));
                log.debug("Added item to cart - StockItem: {}, Quantity: {}", stockItem.getName(), quantity);
                purchaseTableView.refresh();
            } catch (NumberFormatException e) {
                log.warn("Invalid quantity entered, defaulting to 1. Entered value: {}", quantityField.getText());
                shoppingCart.addItem(new SoldItem(stockItem, 1));
                purchaseTableView.refresh();
            } catch (SalesSystemException e) {
                log.info("Error adding item to cart");
                showError("Input error", e.getMessage());
            }
        }
    }

    /**
     * Copied this code from StockController, I think a new class should be made for error windows
     *
     * @param title
     * @param message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        log.info("Displayed error dialog - Title: {}, Message: {}", title, message);
    }

    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        this.addItemButton.setDisable(disable);
        this.barCodeField.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.nameField.setDisable(disable);
        this.priceField.setDisable(disable);
        log.debug("Product fields set to disabled: {}", disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("1");
        nameField.setValue("");
        priceField.setText("");
        log.debug("Product fields reset");
    }
}
