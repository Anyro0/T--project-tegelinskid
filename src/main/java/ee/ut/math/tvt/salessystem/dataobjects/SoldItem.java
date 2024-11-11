package ee.ut.math.tvt.salessystem.dataobjects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */

@Entity
@Table(name ="SOLDITEM")
public class SoldItem {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "SUMMA")
    private double sum;

    @Transient
    private StockItem stockItem;
    @Column(name = "NAME")
    private String name;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private double price;

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


    public Long getId() {
        return id;
    }

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
                "sum=" + sum +
                ", id=" + id +
                ", stockItem=" + stockItem +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}