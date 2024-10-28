import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import static org.junit.jupiter.api.Assertions.*;

import ee.ut.math.tvt.salessystem.logic.StockBasket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;

public class StockBasketTest {
    private SalesSystemDAO dao;

    private StockBasket stockBasket;

    private StockItem stockItem;
    @BeforeEach
    public void setUp() {
        stockItem = new StockItem(81L,"Lasd chips","",2,-5);
        dao = new InMemorySalesSystemDAO();
        stockBasket = new StockBasket(dao);
    }
    @Test
    public void testAddingItemWithNegativeQuantity() {
        assertThrows(SalesSystemException.class, () -> {
            stockBasket.addProductToStock(81L,"Lasd chips",2,-5);
        });
    }
    @Test
    public void testAddingNewItem () {
        assertTrue(stockBasket.addProductToStock(81L, "uusim asi", 2, 1));
    }
    @Test
    public void testAddingExistingItem () {
        StockItem test = dao.findStockItem(1L);
        int exp = test.getQuantity() + 5;
        stockBasket.updateProductQuantity(1L,5,test.getName(),test.getPrice());
        assertEquals(exp,dao.findStockItem(1L).getQuantity());
    }

}
