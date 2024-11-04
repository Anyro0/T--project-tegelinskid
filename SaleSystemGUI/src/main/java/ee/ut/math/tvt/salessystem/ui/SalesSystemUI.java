package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.TeamInfo;
import ee.ut.math.tvt.salessystem.ui.controllers.HistoryController;
import ee.ut.math.tvt.salessystem.ui.controllers.PurchaseController;
import ee.ut.math.tvt.salessystem.ui.controllers.StockController;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.controllers.TeamController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Graphical user interface of the sales system.
 */
public class SalesSystemUI extends Application {

    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    public SalesSystemUI() {
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("SalesSystemUI startup. JavaFX version: {}", System.getProperty("javafx.runtime.version"));

        // Initialize tabs with FXML controllers
        try {
            Tab purchaseTab = createTab("Point-of-sale", "PurchaseTab.fxml", new PurchaseController(dao, shoppingCart));
            Tab stockTab = createTab("Warehouse", "StockTab.fxml", new StockController(dao));
            Tab historyTab = createTab("History", "HistoryTab.fxml", new HistoryController(dao));  // Update controller when available
            Tab teamTab = createTab("Team", "TeamTab.fxml", new TeamController());

            // Set up the root layout
            Group root = new Group();
            Scene scene = new Scene(root, 600, 500, Color.WHITE);
            scene.getStylesheets().add(getClass().getResource("DefaultTheme.css").toExternalForm());

            BorderPane borderPane = new BorderPane();
            borderPane.prefHeightProperty().bind(scene.heightProperty());
            borderPane.prefWidthProperty().bind(scene.widthProperty());
            borderPane.setCenter(new TabPane(purchaseTab, stockTab, historyTab, teamTab)); //add historyTab between  stockTab and teamTAb
            root.getChildren().add(borderPane);

            primaryStage.setTitle("Sales System");
            primaryStage.setScene(scene);
            primaryStage.show();

            log.info("SalesSystem GUI started successfully.");
        } catch (Exception e) {
            log.error("Failed to start SalesSystemUI", e);
        }
    }

    private Tab createTab(String title, String fxml, Initializable controller) {
        Tab tab = new Tab();
        tab.setText(title);
        tab.setClosable(false);

        try {
            log.debug("Loading controls for tab: {}", title);
            Node content = loadControls(fxml, controller);
            tab.setContent(content);
            log.info("Successfully loaded tab: {}", title);
        } catch (IOException e) {
            log.error("Error loading FXML file for tab '{}': {}", title, fxml, e);
        }

        return tab;
    }

    private Node loadControls(String fxml, Initializable controller) throws IOException {
        log.debug("Attempting to load FXML file: {}", fxml);
        URL resource = getClass().getResource(fxml);

        if (resource == null) {
            log.error("FXML file '{}' not found.", fxml);
            throw new IllegalArgumentException(fxml + " not found");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resource);

        if (controller != null) {
            fxmlLoader.setController(controller);
        } else {
            // Use a controller factory for controllers with parameters
            fxmlLoader.setControllerFactory(param -> {
                if (param == HistoryController.class) {
                    return new HistoryController(dao);
                }
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return fxmlLoader.load();
    }
}



