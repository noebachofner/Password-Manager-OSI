import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog zum Hinzufügen oder Bearbeiten von Passwort-Einträgen
 */
public class AddPasswordDialog extends JDialog {
    private JTextField titleField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField websiteField;
    private JTextArea notesArea;

    private PasswordEntry resultEntry = null;
    private boolean isEditMode = false;

    /**
     * Konstruktor für neuen Passwort-Eintrag
     */
    public AddPasswordDialog(JFrame parent) {
        super(parent, "Neues Passwort hinzufügen", true);
        this.isEditMode = false;
        initializeDialog();
    }

    /**
     * Konstruktor für Bearbeitung eines bestehenden Eintrags
     */
    public AddPasswordDialog(JFrame parent, PasswordEntry existingEntry) {
        super(parent, "Passwort bearbeiten", true);
        this.isEditMode = true;
        initializeDialog();
        populateFields(existingEntry);
    }

    private void initializeDialog() {
        initializeComponents();
        layoutComponents();
        setupEventListeners();

        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents() {
        titleField = new JTextField(25);
        usernameField = new JTextField(25);
        passwordField = new JPasswordField(25);
        websiteField = new JTextField(25);
        notesArea = new JTextArea(4, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Hauptpanel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Titel
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel headerLabel = new JLabel(isEditMode ? "Passwort bearbeiten" : "Neues Passwort erstellen");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        mainPanel.add(headerLabel, gbc);

        // Eingabefelder
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;

        // Titel/Name
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Titel/Name: *"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(titleField, gbc);

        // Benutzername
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Benutzername:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(usernameField, gbc);

        // Passwort
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Passwort: *"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        // Passwort-Panel mit Generator-Button
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JButton generateButton = new JButton("🎲");
        generateButton.setToolTipText("Zufälliges Passwort generieren");
        generateButton.setPreferredSize(new Dimension(30, 25));
        generateButton.addActionListener(e -> generateRandomPassword());
        passwordPanel.add(generateButton, BorderLayout.EAST);

        JButton showButton = new JButton("👁");
        showButton.setToolTipText("Passwort anzeigen/verstecken");
        showButton.setPreferredSize(new Dimension(30, 25));
        showButton.addActionListener(e -> togglePasswordVisibility());

        JPanel passwordButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        passwordButtonPanel.add(generateButton);
        passwordButtonPanel.add(showButton);
        passwordPanel.add(passwordButtonPanel, BorderLayout.EAST);

        mainPanel.add(passwordPanel, gbc);

        // Website/URL
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Website/URL:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(websiteField, gbc);

        // Notizen
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Notizen:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(notesScrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton saveButton = new JButton(isEditMode ? "Aktualisieren" : "Speichern");
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.addActionListener(e -> saveEntry());

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.addActionListener(e -> dispose());

        JButton generateAdvancedButton = new JButton("Erweiterte Generierung");
        generateAdvancedButton.addActionListener(e -> showAdvancedGenerator());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(generateAdvancedButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Pflichtfeld-Hinweis
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("* Pflichtfelder");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.NORTH);
    }

    private void setupEventListeners() {
        // Enter-Taste im Titel-Feld springt zum nächsten Feld
        titleField.addActionListener(e -> usernameField.requestFocus());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        websiteField.addActionListener(e -> saveEntry());

        // Automatische Website-Vervollständigung
        websiteField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                autoCompleteWebsite();
            }
        });
    }

    private void populateFields(PasswordEntry entry) {
        if (entry != null) {
            titleField.setText(entry.getTitle());
            usernameField.setText(entry.getUsername());
            passwordField.setText(entry.getPassword());
            websiteField.setText(entry.getWebsite());
            // Notizen-Feld für zukünftige Erweiterung
        }
    }

    private void generateRandomPassword() {
        try {
            String password = PasswordGenerator.generateStandardPassword();
            passwordField.setText(password);

            // Kurze Bestätigung anzeigen
            showTemporaryMessage("Neues Passwort generiert!", 1500);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Fehler bei der Passwort-Generierung: " + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void togglePasswordVisibility() {
        if (passwordField.getEchoChar() == 0) {
            // Passwort verstecken
            passwordField.setEchoChar('*');
        } else {
            // Passwort anzeigen
            passwordField.setEchoChar((char) 0);
        }
    }

    private void showAdvancedGenerator() {
        PasswordGeneratorDialog generatorDialog = new PasswordGeneratorDialog((JFrame) getParent());
        String generatedPassword = generatorDialog.showDialog();

        if (generatedPassword != null && !generatedPassword.isEmpty()) {
            passwordField.setText(generatedPassword);
        }
    }

    private void autoCompleteWebsite() {
        String website = websiteField.getText().trim();

        if (!website.isEmpty() && !website.startsWith("http")) {
            if (!website.startsWith("www.")) {
                website = "www." + website;
            }
            if (!website.contains(".")) {
                website = website + ".com";
            }
            websiteField.setText(website);
        }

        // Auto-fill Titel wenn leer
        if (titleField.getText().trim().isEmpty() && !website.isEmpty()) {
            String siteName = website.replace("www.", "").split("\\.")[0];
            siteName = siteName.substring(0, 1).toUpperCase() + siteName.substring(1);
            titleField.setText(siteName);
        }
    }

    private void saveEntry() {
        // Validierung
        String title = titleField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (title.isEmpty()) {
            showError("Titel ist ein Pflichtfeld!");
            titleField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Passwort ist ein Pflichtfeld!");
            passwordField.requestFocus();
            return;
        }

        // Neuen Eintrag erstellen
        resultEntry = new PasswordEntry();
        resultEntry.setTitle(title);
        resultEntry.setUsername(usernameField.getText().trim());
        resultEntry.setPassword(password);
        resultEntry.setWebsite(websiteField.getText().trim());

        // Bei neuen Einträgen wird Created automatisch gesetzt
        if (!isEditMode) {
            resultEntry.setCreated(new java.util.Date());
        }

        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Eingabefehler", JOptionPane.WARNING_MESSAGE);
    }

    private void showTemporaryMessage(String message, int durationMs) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(new Color(0, 120, 0));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JWindow messageWindow = new JWindow();
        messageWindow.add(messageLabel);
        messageWindow.setSize(200, 30);
        messageWindow.setLocationRelativeTo(this);
        messageWindow.setVisible(true);

        Timer timer = new Timer(durationMs, e -> messageWindow.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Zeigt den Dialog und gibt den erstellten/bearbeiteten Eintrag zurück
     * @return PasswordEntry oder null wenn abgebrochen
     */
    public PasswordEntry showDialog() {
        setVisible(true); // Blockiert bis Dialog geschlossen wird
        return resultEntry;
    }

    /**
     * Gibt den resultierenden Passwort-Eintrag zurück
     */
    public PasswordEntry getPasswordEntry() {
        return resultEntry;
    }
}