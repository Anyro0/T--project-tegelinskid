package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: implement
        startDatePicker.setValue(LocalDate.now());

        // Set end date to tomorrow
        endDatePicker.setValue(LocalDate.now().plusDays(1));
    }
}
