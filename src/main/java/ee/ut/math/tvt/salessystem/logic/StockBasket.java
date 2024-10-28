package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.InvalidPriceException;

import java.util.List;
import java.util.stream.Collectors;

public class StockBasket {

    private final SalesSystemDAO dao;

    public StockBasket(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public boolean addProductToStock(Long id, String name, double price, int quantity) throws SalesSystemException {
        validatePrice(price);
        validateQuantity(quantity);

        List<Long> ids = getListOfIds();
        if (ids.contains(id)) {
            return updateProductQuantity(id, quantity, name, price);
        } else {
            StockItem newItem = new StockItem(id, name, "", price, quantity);
            dao.saveStockItem(newItem);
            return true;
        }
    }

    public Long generateId(String barCode) throws SalesSystemException {
        if (barCode.isEmpty()) {
            List<Long> ids = getListOfIds();
            return ids.isEmpty() ? 1L : ids.stream().max(Long::compare).orElse(0L) + 1;
        } else {
            try {
                return Long.parseLong(barCode);
            } catch (NumberFormatException e) {
                throw new SalesSystemException("Invalid barcode format.", e);
            }
        }
    }

    public List<Long> getListOfIds() {
        List<StockItem> stockItems = dao.findStockItems();
        return stockItems.stream().map(StockItem::getId).collect(Collectors.toList());
    }

    public StockItem getStockItemByBarcode(String barCode) throws SalesSystemException {
        try {
            long code = Long.parseLong(barCode);
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            throw new SalesSystemException("Invalid barcode format.", e);
        }
    }

    public boolean updateProductQuantity(Long id, int newQuantity, String name, double price) throws SalesSystemException {
        validateQuantity(newQuantity);  // Ensure new quantity is valid

        List<StockItem> stockItems = dao.findStockItems();
        for (StockItem item : stockItems) {
            if (item.getId().equals(id)) {
                if (item.getName().equals(name) && item.getPrice() == price) {
                    item.setQuantity(item.getQuantity() + newQuantity);
                    return true;
                }
                throw new SalesSystemException("Item name or price mismatch.");
            }
        }
        throw new SalesSystemException("Product not found in stock.");
    }

    public List<StockItem> getAllStockItems() {
        return dao.findStockItems();
    }

    // New methods to validate price and quantity
    private void validatePrice(double price) throws InvalidPriceException {
        if (price < 0) {
            throw new InvalidPriceException("Price cannot be negative.");
        }
    }

    private void validateQuantity(int quantity) throws SalesSystemException {
        if (quantity <= 0) {
            throw new SalesSystemException("Quantity must be a positive number.");
        }
    }
}
