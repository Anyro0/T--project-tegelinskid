package ee.ut.math.tvt.salessystem.dataobjects;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TeamInfo {
    private String teamName;
    private String contactPerson;
    private String teamMembers;
    private String teamMembersEmails;
    private String cakeImageFilepath;


    public TeamInfo() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new IOException("File not found in classpath: application.properties");
            }

            properties.load(inputStream);

            this.teamName = properties.getProperty("teamName");
            this.contactPerson = properties.getProperty("contactPerson");
            this.teamMembers = properties.getProperty("teamMembers");
            this.teamMembersEmails = properties.getProperty("teamMembersEmails");
            this.cakeImageFilepath = properties.getProperty("cakePhotoFilepath");

        } catch (IOException e) {
            System.err.println("Error reading the properties file: " + e.getMessage());
        }


    }

    public String getTeamName() {
        return teamName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public String getCakeImageFilepath() {
        return cakeImageFilepath;
    }

    public String getTeamMembersEmails() { return teamMembersEmails; }
}
