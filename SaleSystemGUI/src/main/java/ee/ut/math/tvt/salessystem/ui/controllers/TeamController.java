package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dataobjects.TeamInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamController implements Initializable {
    private static final Logger log = LogManager.getLogger(TeamController.class);

    private final TeamInfo teamInfo;

    @FXML
    private Label teamNameLabel;

    @FXML
    private Label contactPersonLabel;

    @FXML
    private Label teamMembersLabel;

    @FXML
    private Label teamMembersEmailsLabel;

    @FXML
    private ImageView cakeImageView;

    public TeamController() {
        this.teamInfo = new TeamInfo();
        log.info("TeamController instantiated with default TeamInfo.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing TeamController");

        teamNameLabel.setText(teamInfo.getTeamName());
        contactPersonLabel.setText(teamInfo.getContactPerson());
        teamMembersLabel.setText(teamInfo.getTeamMembers());
        teamMembersEmailsLabel.setText(teamInfo.getTeamMembersEmails());
        log.debug("Team information set: Name={}, Contact={}, Members={}, Emails={}",
                teamInfo.getTeamName(), teamInfo.getContactPerson(), teamInfo.getTeamMembers(), teamInfo.getTeamMembersEmails());

        // Loading the cake image
        try {
            URL imageUrl = getClass().getClassLoader().getResource("MediaFiles/Cake.png");
            if (imageUrl != null) {
                Image cakeImage = new Image(imageUrl.toExternalForm());
                cakeImageView.setImage(cakeImage);
                log.info("Cake image loaded successfully.");
            } else {
                log.error("Cake image not found at path: MediaFiles/Cake.png");
            }
        } catch (Exception e) {
            log.error("Error loading cake image", e);
        }
    }
}
