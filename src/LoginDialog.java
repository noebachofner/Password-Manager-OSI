import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Vollständiger Anmelde-Dialog für den Passwort-Manager
 * Ermöglicht das Öffnen bestehender Tresore oder das Erstellen neuer Tresore
 */
public class LoginDialog extends JDialog {
    // GUI Komponenten
    private JTextField vaultFileField;
    private JPasswordField masterPasswordField;
    private JButton loginButton;
    private JButton createButton;
    private JButton browseButton;
    private JButton exitButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // Ergebnis-Variablen
    private boolean loginSuccessful = false;
    private File selectedVaultFile = null;
    private String masterPassword = null;
    private boolean isCreatingNew = false;

    public LoginDialog(JFrame parent) {
        super(parent, "Passwort-Manager Anmeldung", true);
        initializeComponents();
        layoutComponents();
        setupEventListeners();

        setSize(520, 320);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Fokus auf Dateifeld setzen
        SwingUtilities.invokeLater(() -> vaultFileField.requestFocus());
    }

    private void initializeComponents() {
        // Textfelder
        vaultFileField = new JTextField(30);
        vaultFileField.setToolTipText("Pfad zur Tresor-Datei (.vault)");

        masterPasswordField = new JPasswordField(30);
        masterPasswordField.setToolTipText("Ihr Master-Passwort für den Tresor");

        // Buttons
        loginButton = new JButton("Tresor öffnen");
        loginButton.setPreferredSize(new Dimension(120, 30));
        loginButton.setToolTipText("Bestehenden Tresor mit Master-Passwort öffnen");

        createButton = new JButton("Neuen Tresor erstellen");
        createButton.setPreferredSize(new Dimension(160, 30));
        createButton.setToolTipText("Neue Tresor-Datei mit Master-Passwort erstellen");

        browseButton = new JButton("Durchsuchen...");
        browseButton.setPreferredSize(new Dimension(120, 25));
        browseButton.setToolTipText("Tresor-Datei auswählen");

        exitButton = new JButton("Beenden");
        exitButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setToolTipText("Anwendung beenden");

        // Status-Komponenten
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.GRAY);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Verarbeite...");

        // Enter-Taste Behandlung
        masterPasswordField.addActionListener(e -> {
            if (new File(vaultFileField.getText().trim()).exists()) {
                attemptLogin();
            } else {
                createNewVault();
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        headerPanel.setBackground(new Color(240, 248, 255));

        // Icon und Titel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));

        JLabel titleLabel = new JLabel("Passwort-Manager");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Wählen Sie eine Tresor-Datei und geben Sie Ihr Master-Passwort ein");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Tresor-Datei Zeile
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel fileLabel = new JLabel("Tresor-Datei:");
        fileLabel.setFont(fileLabel.getFont().deriveFont(Font.BOLD));
        mainPanel.add(fileLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(vaultFileField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(browseButton, gbc);

        // Master-Passwort Zeile
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Master-Passwort:");
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD));
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(masterPasswordField, gbc);

        // Status Zeile
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);

        // Progress Bar
        gbc.gridy = 3;
        mainPanel.add(progressBar, gbc);

        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Haupt-Buttons
        JPanel mainButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        mainButtons.add(loginButton);
        mainButtons.add(createButton);

        // Exit Button
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitPanel.add(exitButton);

        buttonPanel.add(mainButtons, BorderLayout.CENTER);
        buttonPanel.add(exitPanel, BorderLayout.EAST);

        return buttonPanel;
    }

    private void setupEventListeners() {
        // Browse Button
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseForVaultFile();
            }
        });

        // Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Create Button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewVault();
            }
        });

        // Exit Button
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });

        // Dateifeld Änderungen überwachen
        vaultFileField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateButtonStates();
            }
        });

        // Passwort-Feld Änderungen überwachen
        masterPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateButtonStates();
            }
        });

        // Window Closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // Initial Button-Status setzen
        updateButtonStates();
    }

    private void browseForVaultFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Tresor-Datei auswählen oder erstellen");

        // Vault-Dateien Filter
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".vault");
            }

            @Override
            public String getDescription() {
                return "Passwort Tresor Dateien (*.vault)";
            }
        });

        // Standard-Verzeichnis setzen
        String currentPath = vaultFileField.getText().trim();
        if (!currentPath.isEmpty()) {
            File currentFile = new File(currentPath);
            if (currentFile.getParentFile() != null && currentFile.getParentFile().exists()) {
                fileChooser.setCurrentDirectory(currentFile.getParentFile());
                if (currentFile.exists()) {
                    fileChooser.setSelectedFile(currentFile);
                }
            }
        } else {
            // Benutzer-Home-Verzeichnis als Standard
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // .vault Erweiterung hinzufügen falls nicht vorhanden
            if (!selectedFile.getName().toLowerCase().endsWith(".vault")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".vault");
            }

            vaultFileField.setText(selectedFile.getAbsolutePath());
            updateButtonStates();

            // Fokus auf Passwort-Feld setzen
            masterPasswordField.requestFocus();
        }
    }

    private void attemptLogin() {
        String filePath = vaultFileField.getText().trim();
        char[] passwordChars = masterPasswordField.getPassword();

        // Eingaben validieren
        if (!validateInputs(filePath, passwordChars)) {
            return;
        }

        File vaultFile = new File(filePath);
        if (!vaultFile.exists()) {
            updateStatus("Die angegebene Tresor-Datei existiert nicht.", true);
            return;
        }

        // Login-Prozess starten
        performLogin(vaultFile, passwordChars);
    }

    private void createNewVault() {
        char[] passwordChars = masterPasswordField.getPassword();

        if (passwordChars.length == 0) {
            updateStatus("Bitte geben Sie ein Master-Passwort für den neuen Tresor ein.", true);
            masterPasswordField.requestFocus();
            return;
        }

        // Validiere Passwort-Stärke
        String password = new String(passwordChars);
        if (password.length() < 6) {
            updateStatus("Master-Passwort muss mindestens 6 Zeichen lang sein.", true);
            return;
        }

        String filePath = vaultFileField.getText().trim();
        File vaultFile;

        if (filePath.isEmpty()) {
            // File-Dialog für neuen Tresor
            vaultFile = showCreateVaultDialog();
            if (vaultFile == null) return; // Benutzer hat abgebrochen
        } else {
            vaultFile = new File(filePath);
            // .vault Erweiterung hinzufügen falls nicht vorhanden
            if (!vaultFile.getName().toLowerCase().endsWith(".vault")) {
                vaultFile = new File(vaultFile.getAbsolutePath() + ".vault");
            }
        }

        // Überprüfen ob Datei bereits existiert
        if (vaultFile.exists()) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Die Datei '" + vaultFile.getName() + "' existiert bereits.\n" +
                            "Möchten Sie sie überschreiben?",
                    "Datei überschreiben",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Tresor-Erstellungs-Prozess starten
        performCreateVault(vaultFile, passwordChars);
    }

    private File showCreateVaultDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Neuen Tresor erstellen");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

        // Vault-Dateien Filter
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".vault");
            }

            @Override
            public String getDescription() {
                return "Passwort Tresor Dateien (*.vault)";
            }
        });

        // Standard-Dateiname vorschlagen
        fileChooser.setSelectedFile(new File("Meine_Passwörter.vault"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // .vault Erweiterung hinzufügen falls nicht vorhanden
            if (!file.getName().toLowerCase().endsWith(".vault")) {
                file = new File(file.getAbsolutePath() + ".vault");
            }

            return file;
        }

        return null; // Benutzer hat abgebrochen
    }

    private boolean validateInputs(String filePath, char[] password) {
        if (filePath.isEmpty()) {
            updateStatus("Bitte wählen Sie eine Tresor-Datei aus.", true);
            vaultFileField.requestFocus();
            return false;
        }

        if (password.length == 0) {
            updateStatus("Bitte geben Sie Ihr Master-Passwort ein.", true);
            masterPasswordField.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin(File vaultFile, char[] passwordChars) {
        setUIEnabled(false);
        showProgress("Tresor wird geöffnet...");

        // Simulation einer asynchronen Operation
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Hier würde die echte Authentifizierung stattfinden
                Thread.sleep(500); // Simuliere Verarbeitungszeit
                return true; // Simulation: Login immer erfolgreich
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        selectedVaultFile = vaultFile;
                        masterPassword = new String(passwordChars);
                        loginSuccessful = true;
                        isCreatingNew = false;

                        updateStatus("Tresor erfolgreich geöffnet!", false);

                        // Dialog nach kurzer Verzögerung schließen
                        Timer timer = new Timer(800, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();

                    } else {
                        updateStatus("Falsches Master-Passwort oder beschädigte Tresor-Datei.", true);
                        setUIEnabled(true);
                        hideProgress();
                        masterPasswordField.setText("");
                        masterPasswordField.requestFocus();
                    }
                } catch (Exception e) {
                    updateStatus("Fehler beim Öffnen des Tresors: " + e.getMessage(), true);
                    setUIEnabled(true);
                    hideProgress();
                }

                // Passwort aus Speicher löschen
                java.util.Arrays.fill(passwordChars, ' ');
            }
        };

        worker.execute();
    }

    private void performCreateVault(File vaultFile, char[] passwordChars) {
        setUIEnabled(false);
        showProgress("Neuer Tresor wird erstellt...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Hier würde die echte Tresor-Erstellung stattfinden
                Thread.sleep(800); // Simuliere Verarbeitungszeit
                return true; // Simulation: Erstellung immer erfolgreich
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        selectedVaultFile = vaultFile;
                        masterPassword = new String(passwordChars);
                        loginSuccessful = true;
                        isCreatingNew = true;

                        updateStatus("Neuer Tresor erfolgreich erstellt!", false);

                        // Dialog nach kurzer Verzögerung schließen
                        Timer timer = new Timer(1000, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();

                    } else {
                        updateStatus("Fehler beim Erstellen des Tresors.", true);
                        setUIEnabled(true);
                        hideProgress();
                    }
                } catch (Exception e) {
                    updateStatus("Fehler beim Erstellen des Tresors: " + e.getMessage(), true);
                    setUIEnabled(true);
                    hideProgress();
                }

                // Passwort aus Speicher löschen
                java.util.Arrays.fill(passwordChars, ' ');
            }
        };

        worker.execute();
    }

    private void updateButtonStates() {
        String filePath = vaultFileField.getText().trim();
        char[] password = masterPasswordField.getPassword();

        boolean hasFile = !filePath.isEmpty();
        boolean hasPassword = password.length > 0;
        File file = hasFile ? new File(filePath) : null;
        boolean fileExists = file != null && file.exists();

        // Login Button: nur aktiv wenn Datei existiert und Passwort eingegeben
        loginButton.setEnabled(fileExists && hasPassword);

        // Create Button: aktiv wenn Passwort eingegeben (Datei optional)
        createButton.setEnabled(hasPassword);

        // Status-Text aktualisieren
        if (hasFile && !fileExists) {
            updateStatus("Datei existiert nicht - 'Neuen Tresor erstellen' verwenden", false);
        } else if (fileExists && !hasPassword) {
            updateStatus("Master-Passwort eingeben um Tresor zu öffnen", false);
        } else if (!hasFile && hasPassword) {
            updateStatus("Dateipfad eingeben oder 'Neuen Tresor erstellen'", false);
        } else {
            updateStatus(" ", false);
        }

        // Passwort-Array löschen
        java.util.Arrays.fill(password, ' ');
    }

    private void updateStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : Color.GRAY);
    }

    private void showProgress(String message) {
        progressBar.setString(message);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
    }

    private void hideProgress() {
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    private void setUIEnabled(boolean enabled) {
        vaultFileField.setEnabled(enabled);
        masterPasswordField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        createButton.setEnabled(enabled);
        browseButton.setEnabled(enabled);

        if (enabled) {
            updateButtonStates(); // Button-Status neu berechnen
        }
    }

    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Möchten Sie die Anwendung wirklich beenden?",
                "Beenden bestätigen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // Getter-Methoden für die Hauptanwendung
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public File getSelectedVaultFile() {
        return selectedVaultFile;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public boolean isCreatingNewVault() {
        return isCreatingNew;
    }

    /**
     * Zeigt den Dialog und wartet auf Benutzereingabe
     * @return true wenn Login/Erstellung erfolgreich, false wenn abgebrochen
     */
    public boolean showLoginDialog() {
        setVisible(true); // Blockiert bis Dialog geschlossen wird
        return loginSuccessful;
    }

    @Override
    public void dispose() {
        // Sensible Daten aus Speicher löschen
        if (masterPasswordField != null) {
            char[] password = masterPasswordField.getPassword();
            java.util.Arrays.fill(password, ' ');
            masterPasswordField.setText("");
        }

        super.dispose();
    }
}