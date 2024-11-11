package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.TeamInfo;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
        log.info("ConsoleUI initialized with InMemorySalesSystemDAO and ShoppingCart.");
    }

    public static void main(String[] args) throws Exception {
        SalesSystemDAO dao = new HibernateSalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        log.info("Starting Sales System ConsoleUI.");
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        log.info("Running Sales System CLI.");
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            try {
                processCommand(in.readLine().trim().toLowerCase());
            } catch (Exception e) {
                log.error("Error processing command", e);
            }
            System.out.println("Done.");
        }
    }

    private void showStock() {
        log.info("Displaying stock items.");
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getId() + " " + si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (stockItems.isEmpty()) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        log.info("Displaying cart contents.");
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items) Total Sum: " + si.getSum());
        }
        if (cart.getAll().isEmpty()) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void printUsage() {
        log.info("Displaying usage instructions.");
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\tShow this help");
        System.out.println("w\t\tShow warehouse contents");
        System.out.println("c\t\tShow cart contents");
        System.out.println("a IDX NR \tAdd NR of stock item with index IDX to the cart");
        System.out.println("p\t\tPurchase the shopping cart");
        System.out.println("r\t\tReset the shopping cart");
        System.out.println("t\t\tView team info");
        System.out.println("history all\tShow all purchase history");
        System.out.println("history last10\tShow last 10 purchases");
        System.out.println("history dates START END\tShow purchases between dates (format: yyyy-mm-dd)");
        System.out.println("-------------------------");
    }

    private void processCommand(String command) {
        log.info("Processing command: {}", command);
        String[] c = command.split(" ");
        try {
            switch (c[0]) {
                case "h" -> printUsage();
                case "q" -> {
                    log.info("Exiting system.");
                    System.exit(0);
                }
                case "w" -> showStock();
                case "c" -> showCart();
                case "t" -> showTeamInfo();
                case "p" -> {
                    cart.submitCurrentPurchase();
                    log.info("Purchase submitted.");
                }
                case "r" -> {
                    cart.cancelCurrentPurchase();
                    log.info("Cart reset.");
                }
                case "a" -> {
                    if (c.length == 3) {
                        long idx = Long.parseLong(c[1]);
                        int amount = Integer.parseInt(c[2]);
                        log.info("Adding item to cart: Item ID={}, Quantity={}", idx, amount);
                        addItemToCart(idx, amount);
                    } else {
                        log.warn("Invalid add command format: {}", command);
                    }
                }
                case "history" -> {
                    if (c.length == 2 && c[1].equals("all")) showHistoryAll();
                    else if (c.length == 2 && c[1].equals("last10")) showHistoryLast10();
                    else if (c.length == 3) showHistoryBetweenDates(c[1], c[2]);
                }
                default -> {
                    log.warn("Unknown command: {}", command);
                    System.out.println("Unknown command");
                }
            }
        } catch (Exception e) {
            log.error("Error processing command: {}", command, e);
        }
    }

    private void addItemToCart(long idx, int amount) {
        try {
            StockItem item = dao.findStockItem(idx);
            if (item != null) {
                cart.addItem(new SoldItem(item, amount));
                log.info("Item added to cart: {}", item.getName());
            } else {
                log.warn("No stock item with ID {}", idx);
                System.out.println("No stock item with id " + idx);
            }
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error("Error adding item to cart with ID {}: {}", idx, e.getMessage(), e);
        }
    }

    private void showTeamInfo() {
        log.info("Displaying team information.");
        TeamInfo ti = new TeamInfo();
        System.out.println("Team name: " + ti.getTeamName());
        System.out.println("Team Contact Person: " + ti.getContactPerson());
        System.out.println("Team members: " + System.lineSeparator() + ti.getTeamMembers());
        System.out.println("Team members emails: " + System.lineSeparator() + ti.getTeamMembersEmails());
    }
    private void showHistoryAll() {
        List<Purchase> purchases = dao.getPurchaseHistory();
        displayPurchaseHistory(purchases);
    }

    private void showHistoryLast10() {
        List<Purchase> purchases = dao.getPurchaseHistory().stream()
                .sorted(Comparator.comparing(Purchase::getDateTime).reversed())
                .limit(10)
                .collect(Collectors.toList());
        displayPurchaseHistory(purchases);
    }

    private void showHistoryBetweenDates(String start, String end) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            List<Purchase> purchases = dao.getPurchaseHistory().stream()
                    .filter(p -> !p.getDateTime().toLocalDate().isBefore(startDate) &&
                            !p.getDateTime().toLocalDate().isAfter(endDate))
                    .sorted(Comparator.comparing(Purchase::getDateTime).reversed())
                    .collect(Collectors.toList());
            displayPurchaseHistory(purchases);

        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-mm-dd.");
            log.error("Error parsing dates", e);
        }
    }
    private void displayPurchaseHistory(List<Purchase> purchases) {
        if (purchases.isEmpty()) {
            System.out.println("No purchases found.");
        } else {
            System.out.println("Purchase History:");
            for (Purchase purchase : purchases) {
                System.out.println("Date: " + purchase.getDateTime().toLocalDate() +
                        ", Time: " + purchase.getDateTime().toLocalTime() +
                        ", Total: " + purchase.getTotal());

            }
        }
    }
}
