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
            String fileContent = readFileContent(vaultFile);

            // Debug-Ausgabe
            System.out.println("Gelesener Dateiinhalt (erste 100 Zeichen): " +
                    fileContent.substring(0, Math.min(100, fileContent.length())));

            if (fileContent.trim().isEmpty()) {
                // Leere Datei - neuer Tresor
                this.passwords.clear();
                return true;
            }

            // Bereinige den Dateiinhalt (entferne Whitespace und Zeilenumbrüche)
            String cleanedContent = cleanBase64Content(fileContent);

            if (cleanedContent.isEmpty()) {
                // Nach Bereinigung leer - behandle als neuen Tresor
                this.passwords.clear();
                return true;
            }

            // Validiere Base64-Format
            if (!isValidBase64(cleanedContent)) {
                throw new IllegalArgumentException("Ungültiges Base64-Format in Vault-Datei");
            }

            String decryptedData = encryptionManager.decrypt(cleanedContent);
            parsePasswordData(decryptedData);

            return true;

        } catch (Exception e) {
            System.err.println("Fehler beim Laden des Tresors: " + e.getMessage());
            e.printStackTrace();

            // Versuche Recovery-Modus
            return attemptVaultRecovery(vaultFile, masterPassword);
        }
    }

    /**
     * Versucht eine beschädigte Vault-Datei wiederherzustellen
     */
    private boolean attemptVaultRecovery(File vaultFile, String masterPassword) {
        try {
            System.out.println("Versuche Vault-Recovery...");

            // Backup der originalen Datei erstellen
            File backupFile = new File(vaultFile.getAbsolutePath() + ".backup");
            if (!backupFile.exists()) {
                copyFile(vaultFile, backupFile);
                System.out.println("Backup erstellt: " + backupFile.getAbsolutePath());
            }

            // Initialisiere als leeren Tresor
            this.passwords.clear();

            // Frage Benutzer ob neuer Tresor erstellt werden soll
            System.out.println("Vault-Datei konnte nicht gelesen werden. Erstelle neuen leeren Tresor.");

            return true;

        } catch (Exception e) {
            System.err.println("Recovery fehlgeschlagen: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bereinigt Base64-Content von ungültigen Zeichen
     */
    private String cleanBase64Content(String content) {
        if (content == null) return "";

        // Entferne alle Whitespace-Zeichen
        String cleaned = content.replaceAll("\\s+", "");

        // Entferne ungültige Base64-Zeichen (behalte nur A-Z, a-z, 0-9, +, /, =)
        cleaned = cleaned.replaceAll("[^A-Za-z0-9+/=]", "");

        return cleaned;
    }

    /**
     * Überprüft ob ein String gültiges Base64-Format hat
     */
    private boolean isValidBase64(String content) {
        if (content == null || content.isEmpty()) return false;

        // Base64-String sollte nur gültige Zeichen enthalten
        if (!content.matches("^[A-Za-z0-9+/]*={0,2}$")) {
            return false;
        }

        // Länge sollte ein Vielfaches von 4 sein
        return content.length() % 4 == 0;
    }

    /**
     * Kopiert eine Datei
     */
    private void copyFile(File source, File dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
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

            // Validiere das verschlüsselte Ergebnis
            if (!isValidBase64(encryptedData)) {
                throw new IllegalStateException("Verschlüsselung erzeugte ungültiges Base64");
            }

            writeFileContent(currentVaultFile, encryptedData);

            System.out.println("Vault erfolgreich gespeichert: " + currentVaultFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            System.err.println("Fehler beim Speichern des Tresors: " + e.getMessage());
            e.printStackTrace();
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

        if (data == null || data.trim().isEmpty()) {
            return;
        }

        String[] lines = data.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                try {
                    PasswordEntry entry = new PasswordEntry();
                    entry.setTitle(unescapeString(parts[0]));
                    entry.setUsername(unescapeString(parts[1]));
                    entry.setPassword(unescapeString(parts[2]));
                    entry.setWebsite(unescapeString(parts[3]));
                    entry.setCreated(new Date(Long.parseLong(parts[4])));
                    passwords.add(entry);
                } catch (Exception e) {
                    System.err.println("Fehler beim Parsen einer Passwort-Zeile: " + e.getMessage());
                    // Überspringe beschädigte Einträge
                }
            }
        }
    }

    /**
     * Liest den Inhalt einer Datei
     */
    private String readFileContent(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
                // Keine Zeilenumbrüche hinzufügen für Base64-Content
            }

            return content.toString();
        }
    }

    /**
     * Schreibt Inhalt in eine Datei
     */
    private void writeFileContent(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
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

    /**
     * Repariert eine beschädigte Vault-Datei
     */
    public boolean repairVault(File vaultFile, String masterPassword) {
        try {
            System.out.println("Starte Vault-Reparatur...");

            // Backup erstellen
            File backupFile = new File(vaultFile.getAbsolutePath() + ".repair-backup");
            copyFile(vaultFile, backupFile);

            // Neue leere Vault erstellen
            return createNewVault(vaultFile, masterPassword);

        } catch (Exception e) {
            System.err.println("Vault-Reparatur fehlgeschlagen: " + e.getMessage());
            return false;
        }
    }
}