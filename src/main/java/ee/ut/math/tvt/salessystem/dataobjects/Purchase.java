package ee.ut.math.tvt.salessystem.dataobjects;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PURCHASE")
public class Purchase {
    private static long idCounter = 0; // Simple counter to generate unique IDs

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;

    @Column(name = "DATE_TIME", nullable = false)
    private final LocalDateTime dateTime;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<SoldItem> soldItems;

    @Transient
    private final double total;

    // No-arg constructor for JPA
    public Purchase() {
        this.id = ++idCounter;
        this.dateTime = LocalDateTime.now();
        this.soldItems = null;
        this.total = 0.0;
    }

    public Purchase(LocalDateTime now, List<SoldItem> soldItems) {
        this.id = ++idCounter;
        this.dateTime = LocalDateTime.now(); // Set the current date and time
        this.soldItems = soldItems;
        this.total = calculateTotal();
    }

    private double calculateTotal() {
        return soldItems.stream().mapToDouble(SoldItem::getSum).sum();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<SoldItem> getSoldItems() {
        return soldItems;
    }

    public double getTotal() {
        return total;
    }
    public String getDetailedPurchaseInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Purchase Date: ").append(dateTime).append("\n");
        sb.append("Items Bought:\n");
        for (SoldItem item : soldItems) {
            sb.append("- ").append(item.getName())
                    .append(", Quantity: ").append(item.getQuantity())
                    .append(", Price per unit: ").append(item.getPrice())
                    .append(", Total: ").append(item.getSum())
                    .append("\n");
        }
        sb.append("Total Purchase Amount: ").append(total).append("\n");
        return sb.toString();
    }
}
