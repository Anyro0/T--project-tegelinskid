package ee.ut.math.tvt.salessystem.dataobjects;
import jakarta.persistence.*;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */

@Entity
@Table(name ="SOLDITEM")
public class SoldItem {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "SUM")
    private double sum;
    @ManyToOne
    @JoinColumn(name = "STOCK_ITEM_ID", referencedColumnName = "id", nullable = false)
    private StockItem stockItem;
    @Column(name = "NAME")
    private String name;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private double price;
    @ManyToOne
    @JoinColumn(name = "PURCHASE_ID")
    private Purchase purchase;


    public SoldItem() {
    }

    public SoldItem(StockItem stockItem, int quantity) {
        this.stockItem = stockItem;
        this.name = stockItem.getName();
        this.price = stockItem.getPrice();
        this.quantity = quantity;
    }

    public boolean equals(SoldItem newItem){
        return this.stockItem.getId().equals(newItem.getStockItem().getId());
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Purchase getPurchase(){
        return this.purchase;
    }

    public Long getId() {
        return this.id;
    }

    public Long getStockItemID() { return this.stockItem.getId(); }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getSum() {
        return price * ((double) quantity);
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    /*@Override
    public String toString() {
        return String.format("SoldItem{id=%d, name='%s'}", id, name);
    }

     */

    @Override
    public String toString() {
        return "SoldItem{" +
                "id=" + id +
                ", sum=" + sum +
                ", stockItem=" + stockItem +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", purchase=" + purchase +
                '}';
    }
}