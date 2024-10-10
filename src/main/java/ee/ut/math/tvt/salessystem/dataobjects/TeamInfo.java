package ee.ut.math.tvt.salessystem.dataobjects;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class TeamInfo {
    // Declare class variables
    private String teamName;
    private String contactPerson;
    private String teamMembers;
    private String cakePhotoFilepath;

    // Constructor
    public TeamInfo() {
        // Load the file from the classpath
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new IOException("File not found in classpath: application.properties");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                this.teamName = br.readLine();
                this.contactPerson = br.readLine();
                this.teamMembers = br.readLine();
                this.cakePhotoFilepath = br.readLine();
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    // Getter methods
    public String getTeamName() {
        return teamName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public String getCakePhotoFilepath() {
        return cakePhotoFilepath;
    }
}
