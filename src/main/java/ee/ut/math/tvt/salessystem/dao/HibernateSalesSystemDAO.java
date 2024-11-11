package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO{
    private final EntityManagerFactory emf;
    private final EntityManager em;
    public HibernateSalesSystemDAO () {
        // if you get ConnectException / JDBCConnectionException then you
        // probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory ("POS");
        em = emf.createEntityManager ();
    }
    // TODO implement missing methods

    //TODO
    @Override
    public List<StockItem> findStockItems() {
        return null;
    }

    //TODO
    @Override
    public List<String> findStockItemsNames() {
        /*
        List<String> returnable = new ArrayList<>(soldItemList.size());
        for (StockItem stockItem : stockItemList) {
            returnable.add(stockItem.getName());
        }
        return returnable;

         */
        return null;
    }

    //TODO

    @Override
    public void savePurchase(Purchase purchase) {
        /*purchaseHistory.add(purchase);
        List<SoldItem> a = purchase.getSoldItems();

        for (SoldItem soldItem : a) {
            for (StockItem stockItem : stockItemList) {
                if (soldItem.getStockItem().getId() == stockItem.getId()){
                    stockItem.setQuantity(stockItem.getQuantity() - soldItem.getQuantity());
                    break;
                }
            }
        }
        */
    }

    //TODO
    @Override
    public List<Purchase> getPurchaseHistory() {

        //return purchaseHistory;
        return null;
    }

    //TODO
    @Override
    public StockItem findStockItem(long id) {
        /*
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;

         */
        return null;
    }

    //TODO
    @Override
    public StockItem findStockItem(String name){
        /*
        for (StockItem stockItem : stockItemList) {
            if (name.equals(stockItem.getName())) return stockItem;
        }
        return null;
         */
        return null;
    }

    //TODO
    @Override
    public void saveStockItem(StockItem stockItem) {
        //stockItemList.add(stockItem);
        return;
    }

    //TODO
    @Override
    public void saveSoldItem(SoldItem item) {
        //soldItemList.add(item);
    }

    public void close () {
        em.close ();
        emf.close ();
    }

    @Override
    public void beginTransaction () {
        em.getTransaction (). begin ();
    }
    @Override
    public void rollbackTransaction () {
        em.getTransaction (). rollback ();
    }
    @Override
    public void commitTransaction () {
        em.getTransaction (). commit ();
    }
}
