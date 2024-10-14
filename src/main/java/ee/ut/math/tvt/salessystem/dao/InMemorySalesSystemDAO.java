package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;

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
    public List<StockItem> findStockItems() {
        return stockItemList;
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
    public void saveSoldItem(SoldItem item) {
        soldItemList.add(item);
    }

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
