import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;

/**
 * Dialog für die Passwort-Generierung mit anpassbaren Optionen
 */
public class PasswordGeneratorDialog extends JDialog {
    private JCheckBox uppercaseBox;
    private JCheckBox lowercaseBox;
    private JCheckBox numbersBox;
    private JCheckBox symbolsBox;
    private JSpinner lengthSpinner;
    private JTextField generatedPasswordField;
    private JLabel strengthLabel;
    private JProgressBar strengthBar;

    private String lastGeneratedPassword = "";

    public PasswordGeneratorDialog(JFrame parent) {
        super(parent, "Passwort Generator", true);
        initializeComponents();
        layoutComponents();
        setupEventListeners();

        setSize(450, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Initialen Passwort generieren
        generatePassword();
    }

    private void initializeComponents() {
        // Checkboxen für Zeichenarten
        uppercaseBox = new JCheckBox("Großbuchstaben (A-Z)", true);
        lowercaseBox = new JCheckBox("Kleinbuchstaben (a-z)", true);
        numbersBox = new JCheckBox("Zahlen (0-9)", true);
        symbolsBox = new JCheckBox("Sonderzeichen (!@#$%...)", true);

        // Längen-Spinner
        lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
        ((JSpinner.DefaultEditor) lengthSpinner.getEditor()).getTextField().setEditable(false);

        // Ergebnis-Textfeld
        generatedPasswordField = new JTextField();
        generatedPasswordField.setEditable(false);
        generatedPasswordField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        // Stärke-Anzeige
        strengthLabel = new JLabel("Stärke: Sehr stark");
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Haupt-Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Titel
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Sicheres Passwort generieren");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        mainPanel.add(titleLabel, gbc);

        // Optionen-Panel
        gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Zeichen-Optionen"));
        optionsPanel.add(uppercaseBox);
        optionsPanel.add(lowercaseBox);
        optionsPanel.add(numbersBox);
        optionsPanel.add(symbolsBox);
        mainPanel.add(optionsPanel, gbc);

        // Längen-Panel
        gbc.gridy = 2;
        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lengthPanel.add(new JLabel("Passwort-Länge:"));
        lengthPanel.add(lengthSpinner);
        lengthPanel.add(new JLabel("Zeichen"));
        mainPanel.add(lengthPanel, gbc);

        // Ergebnis-Panel
        gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Generiertes Passwort"));
        resultPanel.add(generatedPasswordField, BorderLayout.CENTER);

        // Stärke-Panel
        JPanel strengthPanel = new JPanel(new BorderLayout(5, 0));
        strengthPanel.add(strengthLabel, BorderLayout.WEST);
        strengthPanel.add(strengthBar, BorderLayout.CENTER);
        resultPanel.add(strengthPanel, BorderLayout.SOUTH);

        mainPanel.add(resultPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton generateButton = new JButton("Neues Passwort generieren");
        JButton copyButton = new JButton("In Zwischenablage kopieren");
        JButton useButton = new JButton("Passwort verwenden");
        JButton closeButton = new JButton("Schließen");

        generateButton.addActionListener(e -> generatePassword());
        copyButton.addActionListener(e -> copyToClipboard());
        useButton.addActionListener(e -> usePassword());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(useButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // Event-Listener für automatische Passwort-Generierung bei Änderungen
        ActionListener regenerateListener = e -> generatePassword();

        uppercaseBox.addActionListener(regenerateListener);
        lowercaseBox.addActionListener(regenerateListener);
        numbersBox.addActionListener(regenerateListener);
        symbolsBox.addActionListener(regenerateListener);

        lengthSpinner.addChangeListener(e -> generatePassword());
    }

    private void generatePassword() {
        try {
            boolean includeUppercase = uppercaseBox.isSelected();
            boolean includeLowercase = lowercaseBox.isSelected();
            boolean includeNumbers = numbersBox.isSelected();
            boolean includeSymbols = symbolsBox.isSelected();
            int length = (Integer) lengthSpinner.getValue();

            // Mindestens eine Option muss ausgewählt sein
            if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSymbols) {
                generatedPasswordField.setText("Mindestens eine Zeichen-Option auswählen!");
                strengthLabel.setText("Stärke: Ungültig");
                strengthBar.setValue(0);
                strengthBar.setForeground(Color.RED);
                return;
            }

            String password = PasswordGenerator.generatePassword(
                    includeUppercase, includeLowercase, includeNumbers, includeSymbols, length
            );

            generatedPasswordField.setText(password);
            lastGeneratedPassword = password;

            // Passwort-Stärke berechnen und anzeigen
            updatePasswordStrength(password);

        } catch (Exception e) {
            generatedPasswordField.setText("Fehler bei der Generierung: " + e.getMessage());
            strengthLabel.setText("Stärke: Fehler");
            strengthBar.setValue(0);
            strengthBar.setForeground(Color.RED);
        }
    }

    private void updatePasswordStrength(String password) {
        int strength = PasswordGenerator.calculatePasswordStrength(password);
        String description = PasswordGenerator.getPasswordStrengthDescription(strength);

        strengthLabel.setText("Stärke: " + description + " (" + strength + "%)");
        strengthBar.setValue(strength);

        // Farbe basierend auf Stärke setzen
        if (strength < 30) {
            strengthBar.setForeground(Color.RED);
        } else if (strength < 50) {
            strengthBar.setForeground(Color.ORANGE);
        } else if (strength < 70) {
            strengthBar.setForeground(Color.YELLOW);
        } else if (strength < 90) {
            strengthBar.setForeground(Color.GREEN);
        } else {
            strengthBar.setForeground(new Color(0, 150, 0)); // Dunkelgrün
        }
    }

    private void copyToClipboard() {
        String password = generatedPasswordField.getText();
        if (password != null && !password.isEmpty() && !password.startsWith("Fehler") && !password.startsWith("Mindestens")) {
            try {
                StringSelection selection = new StringSelection(password);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

                JOptionPane.showMessageDialog(
                        this,
                        "Passwort wurde in die Zwischenablage kopiert!",
                        "Erfolgreich kopiert",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Fehler beim Kopieren: " + e.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Kein gültiges Passwort zum Kopieren vorhanden.",
                    "Kein Passwort",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void usePassword() {
        String password = generatedPasswordField.getText();
        if (password != null && !password.isEmpty() && !password.startsWith("Fehler") && !password.startsWith("Mindestens")) {
            // Dialog schließen und Passwort an Hauptanwendung weitergeben
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Kein gültiges Passwort zum Verwenden vorhanden.",
                    "Kein Passwort",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Zeigt den Dialog und gibt das generierte Passwort zurück
     * @return Das ausgewählte Passwort oder null wenn abgebrochen
     */
    public String showDialog() {
        setVisible(true); // Blockiert bis Dialog geschlossen wird
        return lastGeneratedPassword;
    }

    /**
     * Gibt das zuletzt generierte Passwort zurück
     */
    public String getLastGeneratedPassword() {
        return lastGeneratedPassword;
    }

    /**
     * Setzt vordefinierte Einstellungen für verschiedene Passwort-Typen
     */
    public void setPasswordType(PasswordType type) {
        switch (type) {
            case SIMPLE:
                uppercaseBox.setSelected(true);
                lowercaseBox.setSelected(true);
                numbersBox.setSelected(true);
                symbolsBox.setSelected(false);
                lengthSpinner.setValue(12);
                break;
            case COMPLEX:
                uppercaseBox.setSelected(true);
                lowercaseBox.setSelected(true);
                numbersBox.setSelected(true);
                symbolsBox.setSelected(true);
                lengthSpinner.setValue(16);
                break;
            case SECURE:
                uppercaseBox.setSelected(true);
                lowercaseBox.setSelected(true);
                numbersBox.setSelected(true);
                symbolsBox.setSelected(true);
                lengthSpinner.setValue(24);
                break;
            case ALPHANUMERIC:
                uppercaseBox.setSelected(true);
                lowercaseBox.setSelected(true);
                numbersBox.setSelected(true);
                symbolsBox.setSelected(false);
                lengthSpinner.setValue(16);
                break;
        }
        generatePassword();
    }

    /**
     * Enum für verschiedene Passwort-Typen
     */
    public enum PasswordType {
        SIMPLE,      // Buchstaben + Zahlen, 12 Zeichen
        COMPLEX,     // Alle Zeichen, 16 Zeichen
        SECURE,      // Alle Zeichen, 24 Zeichen
        ALPHANUMERIC // Nur Buchstaben + Zahlen, 16 Zeichen
    }
}