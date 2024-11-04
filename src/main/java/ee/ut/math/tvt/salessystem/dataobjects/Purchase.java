package ee.ut.math.tvt.salessystem.dataobjects;

import java.time.LocalDateTime;
import java.util.List;

public class Purchase {
    private LocalDateTime dateTime;
    private List<SoldItem> soldItems;
    private double total;

    public Purchase(LocalDateTime dateTime, List<SoldItem> soldItems) {
        this.dateTime = dateTime;
        this.soldItems = soldItems;
        this.total = calculateTotal();
    }

    private double calculateTotal() {
        return soldItems.stream().mapToDouble(SoldItem::getSum).sum();
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
}

