package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StockBasket {

    private final SalesSystemDAO dao;

    public StockBasket(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public boolean addProductToStock(Long id, String name, double price, int quantity) {
        List<Long> ids = getListOfIds();
        if (ids.contains(id)) {
            return updateProductQuantity(id, quantity, name, price);
        } else {
            StockItem newItem = new StockItem(id, name, "", price, quantity);
            dao.saveStockItem(newItem);
            return true;
        }
    }

    public Long generateId(String barCode) {
        if (barCode.isEmpty()) {
            List<Long> ids = getListOfIds();
            return ids.isEmpty() ? 1L : ids.stream().max(Long::compare).orElse(0L) + 1;
        } else {
            return Long.parseLong(barCode);
        }
    }

    public List<Long> getListOfIds() {
        List<StockItem> stockItems = dao.findStockItems();
        return stockItems.stream().map(StockItem::getId).collect(Collectors.toList());
    }

    public StockItem getStockItemByBarcode(String barCode) {
        try {
            long code = Long.parseLong(barCode);
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean updateProductQuantity(Long id, int newQuantity, String name, double price) {
        List<StockItem> stockItems = dao.findStockItems();
        for (StockItem item : stockItems) {
            if (item.getId().equals(id)) {
                if (item.getName().equals(name) && item.getPrice() == price) {
                    item.setQuantity(item.getQuantity() + newQuantity);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public List<StockItem> getAllStockItems() {
        return dao.findStockItems();
    }
}
