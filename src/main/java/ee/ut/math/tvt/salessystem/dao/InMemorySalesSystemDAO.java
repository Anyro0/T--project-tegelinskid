package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;
    private final List<Purchase> purchaseHistory = new ArrayList<>();


    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<StockItem>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("stockItemListFile.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(";");
                long id = Long.parseLong(elements[0].replace("L", "").trim());
                String name = elements[1].trim();
                String description = elements[2].trim();
                double price = Double.parseDouble(elements[3].trim());
                int quantity = Integer.parseInt(elements[4].trim());
                items.add(new StockItem(id, name, description, price, quantity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();
    }

    @Override
    public List<Purchase> getPurchaseHistory() {
        return purchaseHistory;
    }

    @Override
    public void savePurchase(Purchase purchase) {
        purchaseHistory.add(purchase);
    }



    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public void mergePurchase(Purchase purchase){
        Purchase newPurchase = purchaseHistory.getLast();
        List<SoldItem> newSoldItems = purchase.getSoldItems();
        newPurchase.setSoldItems(newSoldItems);

    }

    @Override
    public void updateQuantity(StockItem stockItem){
        return;
    }

    @Override
    public List<String> findStockItemsNames() {
        /*String[] returnable = new String[soldItemList.size()];
        for (int i = 0; i < soldItemList.size(); i++) {
            returnable[i] = soldItemList.get(i).getName();
        }
        return returnable;

         */
        List<String> returnable = new ArrayList<>(soldItemList.size());
        for (StockItem stockItem : stockItemList) {
            returnable.add(stockItem.getName());
        }
        return returnable;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public StockItem findStockItem(String name){
        for (StockItem stockItem : stockItemList) {
            if (name.equals(stockItem.getName())) return stockItem;
        }
        return null;
    }

    @Override
    public List<Purchase> getLast10HistoryItems(){
        List<Purchase> last10Purchases = this.getPurchaseHistory().stream()
                .sorted(Comparator.comparing(Purchase::getDateTime).reversed())
                .limit(10)
                .collect(Collectors.toList());
        return last10Purchases;
    }

    @Override
    public List<Purchase> getPurchasesBetweenDates(LocalDate startDate, LocalDate endDate) {
        return purchaseHistory.stream()
                .filter(purchase -> {
                    LocalDate purchaseDate = purchase.getDateTime().toLocalDate();
                    return (purchaseDate.isEqual(startDate) || purchaseDate.isAfter(startDate)) &&
                            (purchaseDate.isEqual(endDate) || purchaseDate.isBefore(endDate));
                })
                .sorted(Comparator.comparing(Purchase::getDateTime).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public void saveSoldItem(SoldItem item) { soldItemList.add(item); }

    @Override
    public void saveStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
    }

}
