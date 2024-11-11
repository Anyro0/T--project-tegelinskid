package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);
    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
        log.info("ShoppingCart initialized.");
    }

    /**
     * Add new SoldItem to table or update the table if one of the same item is already in the table
     */
    public void addItem(SoldItem item) {
        log.info("Attempting to add item to cart: {}, quantity: {}", item.getName(), item.getQuantity());
        int warehouseQuantity = dao.findStockItem(item.getStockItem().getId()).getQuantity();
        log.debug("Warehouse quantity for {} is {}", item.getName(), warehouseQuantity);

        if (warehouseQuantity - item.getQuantity() < 0) {
            log.info("Product quantity exceeds available stock for {}. Requested: {}, Available: {}", item.getName(), item.getQuantity(), warehouseQuantity);
            throw new SalesSystemException("Product amount exceeds what is available in stock");
        }

        for (SoldItem itemAlreadyInBasket : items) {
            if (item.equals(itemAlreadyInBasket)) {
                int combinedQuantity = item.getQuantity() + itemAlreadyInBasket.getQuantity();
                if (warehouseQuantity - combinedQuantity < 0) {
                    log.error("Combined quantity of {} exceeds stock. Requested: {}, In cart: {}, Available: {}", item.getName(), item.getQuantity(), itemAlreadyInBasket.getQuantity(), warehouseQuantity);
                    throw new SalesSystemException("Product amount + same product already in cart exceeds what is available in stock");
                } else {
                    itemAlreadyInBasket.setQuantity(combinedQuantity);
                    log.info("Updated quantity for item in cart: {}, new quantity: {}", item.getName(), combinedQuantity);
                    return;
                }
            }
        }

        items.add(item);
        log.info("Added new item to cart: {}, quantity: {}", item.getName(), item.getQuantity());
    }

    public List<SoldItem> getAll() {
        log.debug("Retrieving all items from cart. Current size: {}", items.size());
        return items != null ? items : new ArrayList<>();
    }

    public void cancelCurrentPurchase() {
        log.info("Cancelling current purchase. Clearing {} items from cart.", items.size());
        items.clear();
    }

    public void submitCurrentPurchase() {
        log.info("Submitting current purchase with {} items.", items.size());

        // Create a new Purchase object to record this transaction
        Purchase purchase = new Purchase(LocalDateTime.now(), items);

        dao.beginTransaction();
        try {
            // Save each SoldItem in the transaction
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
                log.debug("Saved item to purchase history: {}, quantity: {}", item.getName(), item.getQuantity());
            }

            // Save the Purchase as a complete transaction
            dao.savePurchase(purchase);

            dao.commitTransaction();
            log.info("Purchase submitted successfully. Clearing cart.");
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            log.info("Error during purchase submission. Transaction rolled back.");
            throw e;
        }
    }

}
