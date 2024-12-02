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

public class StockBasketTest {
    private SalesSystemDAO mockdao;
    private StockBasket mockStockBasket;
    private SalesSystemDAO dao;

    private StockBasket stockBasket;

    @BeforeEach
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        stockBasket = new StockBasket(dao);
        mockdao = mock(SalesSystemDAO.class);
        mockStockBasket = new StockBasket(mockdao);
    }
    @Test
    public void testAddingItemWithNegativeQuantity() {
        assertThrows(SalesSystemException.class, () -> {
            stockBasket.addProductToStock(81L,"Lasd chips",2,-5);
        });
    }
    @Test
    public void testAddingNewItem () {
        int expSize = dao.findStockItems().size() + 1;
        stockBasket.addProductToStock(stockBasket.getListOfIds().getLast()+1,"asi",4,2);
        assertEquals(expSize,dao.findStockItems().size());
    }
    @Test
    public void testAddingExistingItem () {
        StockItem test = dao.findStockItems().getFirst();
        int expQuantity = test.getQuantity() + 5;
        int expSize = dao.findStockItems().size();
        stockBasket.addProductToStock(test.getId(), test.getName(), test.getPrice(),5);
        assertEquals(expQuantity,dao.findStockItem(test.getId()).getQuantity());
        assertEquals(expSize,dao.findStockItems().size());
    }
    @Test
    public void testAddingItemBeginsAndCommitsTransaction () {
        mockStockBasket.addProductToStock(1L,"",1,1);
        verify(mockdao, times(1)).beginTransaction();
        verify(mockdao, times(1)).commitTransaction();
        InOrder inOrder = inOrder(mockdao);
        inOrder.verify(mockdao).beginTransaction();
        inOrder.verify(mockdao).commitTransaction();
    }

}
