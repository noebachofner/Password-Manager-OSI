import java.io.*;
import java.util.*;

/**
 * Verwaltet das Laden und Speichern von Passwort-Tresoren
 * Arbeitet mit verschlüsselten .vault Dateien
 */
public class VaultManager {
    private File currentVaultFile;
    private EncryptionManager encryptionManager;
    private List<PasswordEntry> passwords;

    public VaultManager() {
        this.encryptionManager = new EncryptionManager();
        this.passwords = new ArrayList<>();
    }

    /**
     * Erstellt einen neuen Tresor mit dem angegebenen Master-Passwort
     */
    public boolean createNewVault(File vaultFile, String masterPassword) {
        try {
            encryptionManager.deriveKeyFromPassword(masterPassword);
            this.currentVaultFile = vaultFile;
            this.passwords.clear();

            // Erstelle leere Vault-Datei
            saveVault();
            return true;

        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen des Tresors: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lädt einen existierenden Tresor mit dem Master-Passwort
     */
    public boolean loadVault(File vaultFile, String masterPassword) {
        try {
            if (!vaultFile.exists()) {
                throw new FileNotFoundException("Tresor-Datei nicht gefunden: " + vaultFile.getPath());
            }

            encryptionManager.deriveKeyFromPassword(masterPassword);
            this.currentVaultFile = vaultFile;

            // Lade und entschlüssele Vault-Daten
            String encryptedData = readFileContent(vaultFile);

            if (encryptedData.trim().isEmpty()) {
                // Leere Datei - neuer Tresor
                this.passwords.clear();
                return true;
            }

            String decryptedData = encryptionManager.decrypt(encryptedData);
            parsePasswordData(decryptedData);

            return true;

        } catch (Exception e) {
            System.err.println("Fehler beim Laden des Tresors: " + e.getMessage());
            return false;
        }
    }

    /**
     * Speichert den aktuellen Tresor
     */
    public boolean saveVault() {
        if (currentVaultFile == null || !encryptionManager.hasKey()) {
            System.err.println("Kein Tresor geladen oder kein Schlüssel gesetzt");
            return false;
        }

        try {
            String serializedData = serializePasswordData();
            String encryptedData = encryptionManager.encrypt(serializedData);

            writeFileContent(currentVaultFile, encryptedData);
            return true;

        } catch (Exception e) {
            System.err.println("Fehler beim Speichern des Tresors: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fügt ein neues Passwort zum Tresor hinzu
     */
    public boolean addPassword(PasswordEntry entry) {
        if (entry == null) return false;

        passwords.add(entry);
        return saveVault();
    }

    /**
     * Entfernt ein Passwort aus dem Tresor
     */
    public boolean removePassword(PasswordEntry entry) {
        if (passwords.remove(entry)) {
            return saveVault();
        }
        return false;
    }

    /**
     * Entfernt ein Passwort anhand des Index
     */
    public boolean removePassword(int index) {
        if (index >= 0 && index < passwords.size()) {
            passwords.remove(index);
            return saveVault();
        }
        return false;
    }

    /**
     * Aktualisiert ein existierendes Passwort
     */
    public boolean updatePassword(int index, PasswordEntry updatedEntry) {
        if (index >= 0 && index < passwords.size() && updatedEntry != null) {
            passwords.set(index, updatedEntry);
            return saveVault();
        }
        return false;
    }

    /**
     * Gibt alle Passwörter zurück
     */
    public List<PasswordEntry> getAllPasswords() {
        return new ArrayList<>(passwords);
    }

    /**
     * Sucht nach Passwörtern anhand eines Suchbegriffs
     */
    public List<PasswordEntry> searchPasswords(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPasswords();
        }

        String term = searchTerm.toLowerCase().trim();
        List<PasswordEntry> results = new ArrayList<>();

        for (PasswordEntry entry : passwords) {
            if (entry.getTitle().toLowerCase().contains(term) ||
                    entry.getUsername().toLowerCase().contains(term) ||
                    entry.getWebsite().toLowerCase().contains(term)) {
                results.add(entry);
            }
        }

        return results;
    }

    /**
     * Schließt den aktuellen Tresor
     */
    public void closeVault() {
        this.currentVaultFile = null;
        this.passwords.clear();
        this.encryptionManager.clearKey();
    }

    /**
     * Überprüft ob ein Tresor geladen ist
     */
    public boolean isVaultLoaded() {
        return currentVaultFile != null && encryptionManager.hasKey();
    }

    /**
     * Gibt den Namen der aktuellen Tresor-Datei zurück
     */
    public String getCurrentVaultName() {
        return currentVaultFile != null ? currentVaultFile.getName() : null;
    }

    /**
     * Gibt die Anzahl der gespeicherten Passwörter zurück
     */
    public int getPasswordCount() {
        return passwords.size();
    }

    /**
     * Serialisiert die Passwort-Daten in einen String
     */
    private String serializePasswordData() {
        StringBuilder sb = new StringBuilder();

        for (PasswordEntry entry : passwords) {
            sb.append(escapeString(entry.getTitle())).append("|")
                    .append(escapeString(entry.getUsername())).append("|")
                    .append(escapeString(entry.getPassword())).append("|")
                    .append(escapeString(entry.getWebsite())).append("|")
                    .append(entry.getCreated().getTime()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Parst Passwort-Daten aus einem String
     */
    private void parsePasswordData(String data) {
        passwords.clear();
        String[] lines = data.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                PasswordEntry entry = new PasswordEntry();
                entry.setTitle(unescapeString(parts[0]));
                entry.setUsername(unescapeString(parts[1]));
                entry.setPassword(unescapeString(parts[2]));
                entry.setWebsite(unescapeString(parts[3]));
                entry.setCreated(new Date(Long.parseLong(parts[4])));
                passwords.add(entry);
            }
        }
    }

    /**
     * Liest den Inhalt einer Datei
     */
    private String readFileContent(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            return content.toString();
        }
    }

    /**
     * Schreibt Inhalt in eine Datei
     */
    private void writeFileContent(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    /**
     * Escaped spezielle Zeichen in Strings für die Serialisierung
     */
    private String escapeString(String input) {
        if (input == null) return "";
        return input.replace("|", "\\|").replace("\n", "\\n");
    }

    /**
     * Unescaped spezielle Zeichen nach der Deserialisierung
     */
    private String unescapeString(String input) {
        if (input == null) return "";
        return input.replace("\\|", "|").replace("\\n", "\n");
    }
}