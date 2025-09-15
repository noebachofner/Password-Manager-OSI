import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 * Verwaltet die Verschlüsselung und Entschlüsselung von Daten
 * Verwendet AES-256 Verschlüsselung mit CBC Modus
 */
public class EncryptionManager {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int IV_LENGTH = 16;

    private SecretKey encryptionKey;

    /**
     * Erstellt einen Verschlüsselungsschlüssel aus einem Passwort
     */
    public SecretKey deriveKeyFromPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(password.getBytes("UTF-8"));
        this.encryptionKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        return this.encryptionKey;
    }

    /**
     * Setzt den Verschlüsselungsschlüssel
     */
    public void setEncryptionKey(SecretKey key) {
        this.encryptionKey = key;
    }

    /**
     * Verschlüsselt einen Text und gibt das Ergebnis als Base64 String zurück
     */
    public String encrypt(String plaintext) throws Exception {
        if (encryptionKey == null) {
            throw new IllegalStateException("Verschlüsselungsschlüssel ist nicht gesetzt");
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);

        byte[] iv = cipher.getIV();
        byte[] encryptedData = cipher.doFinal(plaintext.getBytes("UTF-8"));

        // IV und verschlüsselte Daten kombinieren
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Entschlüsselt einen Base64-kodierten verschlüsselten Text
     */
    public String decrypt(String encryptedData) throws Exception {
        if (encryptionKey == null) {
            throw new IllegalStateException("Verschlüsselungsschlüssel ist nicht gesetzt");
        }

        byte[] data = Base64.getDecoder().decode(encryptedData);

        // IV und verschlüsselte Daten trennen
        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[data.length - IV_LENGTH];
        System.arraycopy(data, 0, iv, 0, IV_LENGTH);
        System.arraycopy(data, IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(iv));

        byte[] decryptedData = cipher.doFinal(encrypted);
        return new String(decryptedData, "UTF-8");
    }

    /**
     * Überprüft ob ein Verschlüsselungsschlüssel gesetzt ist
     */
    public boolean hasKey() {
        return encryptionKey != null;
    }

    /**
     * Löscht den aktuellen Verschlüsselungsschlüssel aus dem Speicher
     */
    public void clearKey() {
        this.encryptionKey = null;
    }
}