import java.util.Date;

/**
 * Datenmodell für einen Passwort-Eintrag
 * Enthält alle Informationen zu einem gespeicherten Passwort
 */
public class PasswordEntry {
    private String title;
    private String username;
    private String password;
    private String website;
    private Date created;

    public PasswordEntry() {
        this.created = new Date();
    }

    public PasswordEntry(String title, String username, String password, String website) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.created = new Date();
    }

    // Getter und Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return String.format("PasswordEntry{title='%s', username='%s', website='%s', created=%s}",
                title, username, website, created);
    }
}