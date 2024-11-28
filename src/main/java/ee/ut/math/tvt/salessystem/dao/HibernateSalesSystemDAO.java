package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public void updateQuantity(StockItem stockItem) {
        em.merge(stockItem);
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
    public List<Purchase> getLast10HistoryItems() {
        return em.createQuery("FROM Purchase ORDER BY id DESC", Purchase.class)
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public List<Purchase> getPurchasesBetweenDates(LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime for accurate comparison
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        return em.createQuery(
                        "FROM Purchase p WHERE p.dateTime BETWEEN :start AND :end ORDER BY p.dateTime DESC",
                        Purchase.class)
                .setParameter("start", startDateTime)
                .setParameter("end", endDateTime)
                .getResultList();
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
