import java.security.SecureRandom;

/**
 * Sichere Passwort-Generierung mit konfigurierbaren Optionen
 */
public class PasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generiert ein zufälliges Passwort mit den angegebenen Parametern
     *
     * @param includeUppercase Großbuchstaben einschließen
     * @param includeLowercase Kleinbuchstaben einschließen
     * @param includeNumbers Zahlen einschließen
     * @param includeSymbols Sonderzeichen einschließen
     * @param length Gewünschte Passwort-Länge
     * @return Generiertes Passwort
     */
    public static String generatePassword(boolean includeUppercase, boolean includeLowercase,
                                          boolean includeNumbers, boolean includeSymbols, int length) {

        if (length < 1) {
            throw new IllegalArgumentException("Passwort-Länge muss mindestens 1 sein");
        }

        StringBuilder charset = new StringBuilder();

        if (includeUppercase) charset.append(UPPERCASE);
        if (includeLowercase) charset.append(LOWERCASE);
        if (includeNumbers) charset.append(NUMBERS);
        if (includeSymbols) charset.append(SYMBOLS);

        if (charset.length() == 0) {
            throw new IllegalArgumentException("Mindestens eine Zeichenart muss ausgewählt werden");
        }

        StringBuilder password = new StringBuilder();

        // Stelle sicher, dass mindestens ein Zeichen jeder gewählten Kategorie enthalten ist
        if (includeUppercase && length > 0) {
            password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (includeLowercase && length > password.length()) {
            password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (includeNumbers && length > password.length()) {
            password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        if (includeSymbols && length > password.length()) {
            password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }

        // Fülle den Rest mit zufälligen Zeichen aus dem gesamten Charset
        while (password.length() < length) {
            int index = random.nextInt(charset.length());
            password.append(charset.charAt(index));
        }

        // Mische das Passwort, um die Reihenfolge zu randomisieren
        return shuffleString(password.toString());
    }

    /**
     * Generiert ein Standard-Passwort mit allen Zeichentypen und 16 Zeichen Länge
     */
    public static String generateStandardPassword() {
        return generatePassword(true, true, true, true, 16);
    }

    /**
     * Generiert ein einfaches Passwort nur mit Buchstaben und Zahlen
     */
    public static String generateSimplePassword(int length) {
        return generatePassword(true, true, true, false, length);
    }

    /**
     * Generiert ein komplexes Passwort mit allen verfügbaren Zeichen
     */
    public static String generateComplexPassword(int length) {
        return generatePassword(true, true, true, true, length);
    }

    /**
     * Mischt die Zeichen eines Strings zufällig
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();

        // Fisher-Yates Shuffle Algorithmus
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }

        return new String(characters);
    }

    /**
     * Bewertet die Stärke eines Passworts (0-100)
     */
    public static int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Länge bewerten
        if (password.length() >= 8) score += 25;
        if (password.length() >= 12) score += 25;
        if (password.length() >= 16) score += 10;

        // Zeichenvielfalt bewerten
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*");

        if (hasUpper) score += 10;
        if (hasLower) score += 10;
        if (hasNumber) score += 10;
        if (hasSymbol) score += 10;

        return Math.min(100, score);
    }

    /**
     * Gibt eine textuelle Beschreibung der Passwort-Stärke zurück
     */
    public static String getPasswordStrengthDescription(int strength) {
        if (strength < 30) return "Sehr schwach";
        if (strength < 50) return "Schwach";
        if (strength < 70) return "Mittel";
        if (strength < 90) return "Stark";
        return "Sehr stark";
    }
}