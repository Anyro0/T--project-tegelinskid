package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
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

    @Override
    public List<StockItem> findStockItems() {
        return em.createQuery("FROM StockItem", StockItem.class).getResultList();
    }

    @Override
    public List<String> findStockItemsNames() {
        return em.createQuery("SELECT s.name FROM StockItem s", String.class).getResultList();
    }
    @Override
    public void updateQuantity(Purchase purchase) {
        for (SoldItem soldItem : purchase.getSoldItems()) {
            StockItem stockItem = soldItem.getStockItem();
            int newQuantity = stockItem.getQuantity() - soldItem.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Not enough stock for item: " + stockItem.getName());
            }
            stockItem.setQuantity(newQuantity);
            em.merge(stockItem);
        }
    }

    @Override
    public void savePurchase(Purchase purchase) {
        em.persist(purchase);
    }

    @Override
    public void mergePurchase(Purchase purchase){
        em.merge(purchase);
    }

    @Override
    public List<Purchase> getPurchaseHistory() {
        return em.createQuery("FROM Purchase", Purchase.class).getResultList();
    }

    @Override
    public StockItem findStockItem(long id) {
        return em.find(StockItem.class, id);
    }

    @Override
    public StockItem findStockItem(String name) {
        try {
            return em.createQuery("FROM StockItem s WHERE s.name = :name", StockItem.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        if (stockItem.getId() == null || em.find(StockItem.class, stockItem.getId()) == null) {
            em.persist(stockItem);
        } else {
            em.merge(stockItem);
        }
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        em.persist(item);
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
