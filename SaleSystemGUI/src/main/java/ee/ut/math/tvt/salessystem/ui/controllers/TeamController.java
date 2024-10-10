package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dataobjects.TeamInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamController implements Initializable {

    private final TeamInfo teamInfo;

    @FXML
    private Label teamNameLabel;

    @FXML
    private Label contactPersonLabel;

    @FXML
    private Label teamMembersLabel;

    @FXML
    private ImageView cakeImageView;

    public TeamController() {
        this.teamInfo = new TeamInfo();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamNameLabel.setText(teamInfo.getTeamName());
        contactPersonLabel.setText(teamInfo.getContactPerson());
        teamMembersLabel.setText(teamInfo.getTeamMembers());

        //This code should be looked over again, I had problems getting the Image as an Image class here and MediaFiles is not reachable from here.
        Image cakeImage = new Image(getClass().getClassLoader().getResource("MediaFiles/Cake.png").toExternalForm());
        cakeImageView.setImage(cakeImage);
    }
}
