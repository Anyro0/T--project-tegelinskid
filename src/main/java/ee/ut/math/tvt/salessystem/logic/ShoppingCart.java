package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table or update the table if one of the same item is already in the table
     */
    public void addItem(SoldItem item) {

        int warehouseQuantity = dao.findStockItem(item.getStockItem().getId()).getQuantity();
        if (warehouseQuantity - item.getQuantity() < 0) throw new SalesSystemException("Product amount exceeds what is available in stock");

        for (SoldItem itemAlreadyInBasket : items) {
            if (item.equals(itemAlreadyInBasket)) {
                if (warehouseQuantity - (item.getQuantity() + itemAlreadyInBasket.getQuantity()) < 0)
                    throw new SalesSystemException("Product amount + same product already in cart exceeds what is available in stock");
                else {
                itemAlreadyInBasket.setQuantity(itemAlreadyInBasket.getQuantity() + item.getQuantity());
                return;
                }
            }
        }

        items.add(item);
        //log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        // TODO decrease quantities of the warehouse stock

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        dao.beginTransaction();
        try {
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
            }
            dao.commitTransaction();
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}
