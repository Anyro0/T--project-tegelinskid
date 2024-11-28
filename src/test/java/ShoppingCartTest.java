import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.StockBasket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;

import java.time.LocalDateTime;

public class ShoppingCartTest {
    private SalesSystemDAO mockdao;
    private ShoppingCart mockShoppingCart;
    private SalesSystemDAO dao;

    private ShoppingCart shoppingCart;
    private StockItem stockItem;
    private SoldItem soldItem;
    private Purchase purchase;

    @BeforeEach
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
        mockdao = mock(SalesSystemDAO.class);
        mockShoppingCart = new ShoppingCart(mockdao);
        stockItem = dao.findStockItems().getFirst();
        soldItem = new SoldItem(stockItem,1);
    }
    @Test
    public void testAddingExistingItem () {
        //check that adding an existing item increases
        //the quantity
        int exp = 2;

        shoppingCart.addItem(soldItem);
        shoppingCart.addItem(soldItem);

        assertEquals(exp,shoppingCart.getAll().getFirst().getQuantity());

    }
    @Test
    public void testAddingNewItem() {
        //- check that the new item is added to the shopping
        //cart
        shoppingCart.addItem(soldItem);

        assertEquals(1,shoppingCart.getAll().size());


    }
    @Test
    public void testAddingItemWithNegativeQuantity() {
        //check that an exception is
        //thrown if trying to add an item with a negative quantity
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.addItem(new SoldItem(stockItem,-1));
        });
    }
    @Test
    public void testAddingItemWithQuantityTooLarge () {
        //check that an exception is
        //thrown if the quantity of the added item is larger than the quantity in the
        //warehouse
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.addItem(new SoldItem(stockItem,stockItem.getQuantity()+1));
        });
    }
    @Test
    public void testAddingItemWithQuantitySumTooLarge() {
        //check that an
        //exception is thrown if the sum of the quantity of the added item and the quantity
        //already in the shopping cart is larger than the quantity in the warehouse
        shoppingCart.addItem(soldItem);

        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.addItem(new SoldItem(stockItem,stockItem.getQuantity()));
        });
    }
    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity () {
        //- check that submitting the current purchase decreases the quantity of all StockItems
        int exp = stockItem.getQuantity() - soldItem.getQuantity();

        shoppingCart.addItem(soldItem);
        shoppingCart.submitCurrentPurchase();

        assertEquals(exp,stockItem.getQuantity());
    }
    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() {
        // check that submitting the current purchase calls beginTransaction and
        //endTransaction, exactly once and in that order
        SoldItem soldItem1 = mock(SoldItem.class);
        when(soldItem1.getQuantity()).thenReturn(10);
        when(soldItem1.getStockItem()).thenReturn(new StockItem(1L, "Test Item", "Test Description", 10.0, 10));
        when(mockdao.findStockItem(anyLong())).thenReturn(new StockItem(1L, "Test Item", "Test Description", 10.0, 10));


        mockShoppingCart.addItem(soldItem1);

        mockShoppingCart.submitCurrentPurchase();

        verify(mockdao,times(1)).beginTransaction();
        verify(mockdao,times(1)).commitTransaction();

        InOrder inOrder = inOrder(mockdao);

        inOrder.verify(mockdao).beginTransaction();
        inOrder.verify(mockdao).commitTransaction();
    }
    @Test
    public void testSubmittingCurrentOrderCreatesPurchase() {
        //check that
        //a new HistoryItem is saved and that it contains the correct SoldItems
        shoppingCart.addItem(soldItem);
        shoppingCart.submitCurrentPurchase();

        assertEquals(dao.getPurchaseHistory().getFirst().getSoldItems().getFirst(),soldItem);

    }
    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime() {
        //check that the
        //timestamp on the created HistoryItem is set correctly (for example has only a
        //small difference to the current time)
        shoppingCart.addItem(soldItem);

        LocalDateTime dateTimeBefore = LocalDateTime.now();
        shoppingCart.submitCurrentPurchase();
        LocalDateTime dateTimeAfter = LocalDateTime.now();
        //Kontrollib et salvestatud aeg ei oleks enne dateTimeBefore-i ega pärast dateTimeAfter-it
        assertTrue(!dao.getPurchaseHistory().getFirst().getDateTime().isBefore(dateTimeBefore)
                && !dao.getPurchaseHistory().getFirst().getDateTime().isAfter(dateTimeAfter));

    }
    @Test
    public void testCancellingOrder () {
        //check that canceling an order (with some items)
        //and then submitting a new order (with some different items) only saves the items
        //from the new order (with canceled items are discarded)
        SoldItem soldItem2 = new SoldItem(dao.findStockItem(2l),1);

        shoppingCart.addItem(soldItem);
        shoppingCart.cancelCurrentPurchase();

        shoppingCart.addItem(soldItem2);
        shoppingCart.submitCurrentPurchase();

        assertEquals(dao.getPurchaseHistory().size(),1);
        assertEquals(dao.getPurchaseHistory().getFirst().getSoldItems().size(),1);
        assertEquals(dao.getPurchaseHistory().getFirst().getSoldItems().getFirst(),soldItem2);

    }
    @Test
    public void testCancellingOrderQuanititesUnchanged() {
        //check that after
        //canceling an order the quantities of the related StockItems are not changed
        int exp = stockItem.getQuantity();

        shoppingCart.addItem(soldItem);
        shoppingCart.cancelCurrentPurchase();

        assertEquals(exp,stockItem.getQuantity());
    }

}
