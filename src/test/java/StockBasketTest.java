import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ee.ut.math.tvt.salessystem.logic.StockBasket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.List;

public class StockBasketTest {
    @Mock
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
        List<StockItem> ids = dao.findStockItems();
        assertTrue(stockBasket.addProductToStock(ids.getLast().getId()+1, "uusim asi", 2, 1));
    }
    @Test
    public void testAddingExistingItem () {
        StockItem test = dao.findStockItem(1L);
        int exp = test.getQuantity() + 5;
        stockBasket.updateProductQuantity(1L,5,test.getName(),test.getPrice());
        assertEquals(exp,dao.findStockItem(1L).getQuantity());
    }
    @Test
    public void testAddingItemBeginsAndCommitsTransaction () {
        SalesSystemDAO mockdao = mock(SalesSystemDAO.class);
        stockBasket = new StockBasket(mockdao);
        stockBasket.addProductToStock(1L,"",1,1);
        verify(mockdao, times(1)).beginTransaction();
        verify(mockdao, times(1)).commitTransaction();
        InOrder inOrder = inOrder(mockdao);
        inOrder.verify(mockdao).beginTransaction();
        inOrder.verify(mockdao).commitTransaction();
    }

}
