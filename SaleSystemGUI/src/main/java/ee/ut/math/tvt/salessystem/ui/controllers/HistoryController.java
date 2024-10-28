package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {
    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing HistoryController");

        try {
            log.debug("HistoryController initialization details - location: {}, resources: {}", location, resources);

            startDatePicker.setValue(LocalDate.now());

            // Set end date to tomorrow
            endDatePicker.setValue(LocalDate.now().plusDays(1));

            log.info("HistoryController initialized successfully");
        } catch (Exception e) {
            log.error("Error during HistoryController initialization", e);
        }
    }
}
