package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {
    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing HistoryController");

        try {
            log.debug("HistoryController initialization details - location: {}, resources: {}", location, resources);

            // Future implementation of initialization logic can go here

            log.info("HistoryController initialized successfully");
        } catch (Exception e) {
            log.error("Error during HistoryController initialization", e);
        }
    }
}
