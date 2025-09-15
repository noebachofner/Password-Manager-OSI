import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;

/**
 * Hauptklasse des sicheren Passwort-Managers
 * Koordiniert alle Komponenten und stellt die Benutzeroberfläche bereit
 */
public class PasswordManager extends JFrame {
    // Komponenten
    private JTable passwordTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;

    // Manager-Klassen
    private VaultManager vaultManager;

    // Status
    private boolean isLoggedIn = false;

    public PasswordManager() {
        vaultManager = new VaultManager();
        initializeGUI();
        showLoginDialog();
    }

    private void initializeGUI() {
        setTitle("Sicherer Passwort-Manager");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        // Window Closing Event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        createMenuBar();
        createMainPanel();
        createStatusBar();

        // Initial deaktiviert bis Login
        setComponentsEnabled(false);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Datei-Menü
        JMenu fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic(KeyEvent.VK_D);

        JMenuItem newVault = new JMenuItem("Neuer Tresor");
        newVault.setMnemonic(KeyEvent.VK_N);
        newVault.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newVault.addActionListener(e -> createNewVault());

        JMenuItem openVault = new JMenuItem("Tresor öffnen");
        openVault.setMnemonic(KeyEvent.VK_O);
        openVault.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openVault.addActionListener(e -> openVault());

        JMenuItem saveVault = new JMenuItem("Tresor speichern");
        saveVault.setMnemonic(KeyEvent.VK_S);
        saveVault.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveVault.addActionListener(e -> saveVault());

        JMenuItem logout = new JMenuItem("Abmelden");
        logout.setMnemonic(KeyEvent.VK_A);
        logout.addActionListener(e -> logout());

        JMenuItem exit = new JMenuItem("Beenden");
        exit.setMnemonic(KeyEvent.VK_B);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exit.addActionListener(e -> exitApplication());

        fileMenu.add(newVault);
        fileMenu.add(openVault);
        fileMenu.addSeparator();
        fileMenu.add(saveVault);
        fileMenu.addSeparator();
        fileMenu.add(logout);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        // Bearbeiten-Menü
        JMenu editMenu = new JMenu("Bearbeiten");
        editMenu.setMnemonic(KeyEvent.VK_B);

        JMenuItem addPassword = new JMenuItem("Neues Passwort");
        addPassword.setMnemonic(KeyEvent.VK_N);
        addPassword.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK));
        addPassword.addActionListener(e -> showAddPasswordDialog());

        JMenuItem generatePassword = new JMenuItem("Passwort generieren");
        generatePassword.setMnemonic(KeyEvent.VK_G);
        generatePassword.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        generatePassword.addActionListener(e -> showPasswordGenerator());

        editMenu.add(addPassword);
        editMenu.add(generatePassword);

        // Tools-Menü
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem checkFields = new JMenuItem("Passwort-Felder prüfen");
        checkFields.setMnemonic(KeyEvent.VK_P);
        checkFields.addActionListener(e -> simulateFieldDetection());

        JMenuItem importData = new JMenuItem("Daten importieren");
        importData.setMnemonic(KeyEvent.VK_I);
        importData.addActionListener(e -> showInfo("Import-Funktion noch nicht implementiert"));

        JMenuItem exportData = new JMenuItem("Daten exportieren");
        exportData.setMnemonic(KeyEvent.VK_E);
        exportData.addActionListener(e -> showInfo("Export-Funktion noch nicht implementiert"));

        toolsMenu.add(checkFields);
        toolsMenu.addSeparator();
        toolsMenu.add(importData);
        toolsMenu.add(exportData);

        // Hilfe-Menü
        JMenu helpMenu = new JMenu("Hilfe");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem about = new JMenuItem("Über");
        about.setMnemonic(KeyEvent.VK_U);
        about.addActionListener(e -> showAboutDialog());

        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Such- und Button-Panel
        JPanel topPanel = new JPanel(new BorderLayout());

        // Such-Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Suchen:"));
        searchField = new JTextField(25);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterPasswords();
            }
        });
        searchPanel.add(searchField);

        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Neues Passwort");
        addButton.setIcon(createIcon("+")); // Unicode Plus
        addButton.addActionListener(e -> showAddPasswordDialog());

        JButton generateButton = new JButton("Generator");
        generateButton.setIcon(createIcon("⚙")); // Unicode Gear
        generateButton.addActionListener(e -> showPasswordGenerator());

        buttonPanel.add(addButton);
        buttonPanel.add(generateButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Passwort-Tabelle
        createPasswordTable();
        JScrollPane scrollPane = new JScrollPane(passwordTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Gespeicherte Passwörter"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void createPasswordTable() {
        String[] columns = {"Titel", "Benutzername", "Website", "Erstellt", "Aktionen"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Nur Actions-Spalte editierbar
            }
        };

        passwordTable = new JTable(tableModel);
        passwordTable.setRowHeight(35);
        passwordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        passwordTable.getTableHeader().setReorderingAllowed(false);

        // Spaltenbreiten setzen
        passwordTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Titel
        passwordTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Benutzername
        passwordTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Website
        passwordTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Erstellt
        passwordTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Aktionen

        // Action-Spalte Renderer und Editor
        passwordTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        passwordTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());

        statusLabel = new JLabel(" Bereit - Kein Tresor geladen");
        statusLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private Icon createIcon(String text) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                g.drawString(text, x + 2, y + 12);
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this);

        if (loginDialog.showLoginDialog()) {
            String masterPassword = loginDialog.getMasterPassword();
            java.io.File vaultFile = loginDialog.getSelectedVaultFile();

            boolean success;
            if (vaultFile.exists()) {
                // Bestehenden Tresor laden
                success = vaultManager.loadVault(vaultFile, masterPassword);
            } else {
                // Neuen Tresor erstellen
                success = vaultManager.createNewVault(vaultFile, masterPassword);
            }

            if (success) {
                isLoggedIn = true;
                setComponentsEnabled(true);
                updatePasswordTable();
                updateStatusBar();
                setTitle("Passwort-Manager - " + vaultManager.getCurrentVaultName());

                if (!vaultFile.exists()) {
                    showInfo("Neuer Tresor erfolgreich erstellt!");
                }
            } else {
                showError("Fehler beim Laden/Erstellen des Tresors!");
                System.exit(1);
            }
        } else {
            // Benutzer hat Anmeldung abgebrochen
            System.exit(0);
        }
    }

    private void showAddPasswordDialog() {
        AddPasswordDialog dialog = new AddPasswordDialog(this);
        PasswordEntry newEntry = dialog.showDialog();

        if (newEntry != null) {
            if (vaultManager.addPassword(newEntry)) {
                updatePasswordTable();
                updateStatusBar();
                showInfo("Passwort erfolgreich hinzugefügt!");
            } else {
                showError("Fehler beim Speichern des Passworts!");
            }
        }
    }

    private void showPasswordGenerator() {
        PasswordGeneratorDialog dialog = new PasswordGeneratorDialog(this);
        dialog.setVisible(true);
    }

    private void simulateFieldDetection() {
        // Simuliert die Erkennung von Passwort-Feldern in Webbrowsern/Anwendungen
        int option = JOptionPane.showConfirmDialog(
                this,
                "🔍 Passwort-Feld erkannt!\n\n" +
                        "Website: www.beispiel.de\n" +
                        "Feld-Typ: Passwort\n\n" +
                        "Möchten Sie ein neues Passwort generieren und automatisch einfügen?",
                "Passwort-Feld Erkennung",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            handleAutomaticPasswordGeneration();
        }
    }

    private void handleAutomaticPasswordGeneration() {
        // Dialog für automatische Passwort-Generierung
        JDialog autoDialog = new JDialog(this, "Automatisches Passwort", true);
        autoDialog.setSize(400, 250);
        autoDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Generiere automatisch ein Passwort
        String generatedPassword = PasswordGenerator.generateStandardPassword();

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Automatisch generiertes Passwort:"), gbc);

        gbc.gridy = 1;
        JTextField passwordField = new JTextField(generatedPassword, 25);
        passwordField.setEditable(false);
        passwordField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        panel.add(passwordField, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Benutzername:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField("", 15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Website/App:"), gbc);
        gbc.gridx = 1;
        JTextField websiteField = new JTextField("www.beispiel.de", 15);
        panel.add(websiteField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton insertButton = new JButton("Einfügen & Speichern");
        JButton regenerateButton = new JButton("Neu generieren");
        JButton cancelButton = new JButton("Abbrechen");

        insertButton.addActionListener(e -> {
            // Simuliere automatisches Einfügen
            showInfo("✅ Passwort wurde automatisch eingefügt!");

            // Speichere das Passwort
            String username = usernameField.getText().trim();
            String website = websiteField.getText().trim();

            if (!username.isEmpty() && !website.isEmpty()) {
                PasswordEntry entry = new PasswordEntry(website, username, generatedPassword, website);
                if (vaultManager.addPassword(entry)) {
                    updatePasswordTable();
                    updateStatusBar();
                    showInfo("Passwort erfolgreich gespeichert!");
                }
            }
            autoDialog.dispose();
        });

        regenerateButton.addActionListener(e -> {
            String newPassword = PasswordGenerator.generateStandardPassword();
            passwordField.setText(newPassword);
        });

        cancelButton.addActionListener(e -> autoDialog.dispose());

        buttonPanel.add(insertButton);
        buttonPanel.add(regenerateButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        autoDialog.add(panel);
        autoDialog.setVisible(true);
    }

    private void updatePasswordTable() {
        tableModel.setRowCount(0);

        List<PasswordEntry> passwords = vaultManager.searchPasswords(searchField.getText());

        for (PasswordEntry entry : passwords) {
            tableModel.addRow(new Object[]{
                    entry.getTitle(),
                    entry.getUsername(),
                    entry.getWebsite(),
                    formatDate(entry.getCreated()),
                    "Aktionen"
            });
        }
    }

    private void filterPasswords() {
        updatePasswordTable();
    }

    private String formatDate(Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatter.format(date);
    }

    private void createNewVault() {
        logout();
        showLoginDialog();
    }

    private void openVault() {
        logout();
        showLoginDialog();
    }

    private void saveVault() {
        if (vaultManager.saveVault()) {
            updateStatusBar();
            showInfo("Tresor erfolgreich gespeichert!");
        } else {
            showError("Fehler beim Speichern des Tresors!");
        }
    }

    private void logout() {
        vaultManager.closeVault();
        isLoggedIn = false;
        setComponentsEnabled(false);
        updatePasswordTable();
        updateStatusBar();
        setTitle("Sicherer Passwort-Manager");
        showLoginDialog();
    }

    private void exitApplication() {
        if (isLoggedIn) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Möchten Sie vor dem Beenden speichern?",
                    "Beenden bestätigen",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }

            if (option == JOptionPane.YES_OPTION) {
                vaultManager.saveVault();
            }
        }

        System.exit(0);
    }

    private void setComponentsEnabled(boolean enabled) {
        // Hier könnten spezifische Komponenten aktiviert/deaktiviert werden
        // Vorerst wird das über die isLoggedIn Variable gesteuert
    }

    private void updateStatusBar() {
        if (isLoggedIn && vaultManager.isVaultLoaded()) {
            String vaultName = vaultManager.getCurrentVaultName();
            int passwordCount = vaultManager.getPasswordCount();
            statusLabel.setText(String.format(" Tresor: %s | Passwörter: %d", vaultName, passwordCount));
        } else {
            statusLabel.setText(" Bereit - Kein Tresor geladen");
        }
    }

    private void showAboutDialog() {
        String message = """
            Sicherer Passwort-Manager v1.0
            
            Ein sicherer und benutzerfreundlicher Passwort-Manager
            mit AES-256 Verschlüsselung.
            
            Features:
            • Sichere AES-256 Verschlüsselung
            • Starke Passwort-Generierung
            • Automatische Feld-Erkennung
            • Mehrere Tresor-Dateien
            • Sichere Master-Passwort Authentifizierung
            
            Entwickelt in Java mit Swing GUI.
            """;

        JOptionPane.showMessageDialog(
                this,
                message,
                "Über Passwort-Manager",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // Button Renderer für die Aktions-Spalte
    private class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton viewButton = new JButton("👁");
        private JButton copyButton = new JButton("📋");
        private JButton editButton = new JButton("✏");
        private JButton deleteButton = new JButton("🗑");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));

            // Tooltips für bessere Benutzerfreundlichkeit
            viewButton.setToolTipText("Passwort anzeigen");
            copyButton.setToolTipText("Passwort kopieren");
            editButton.setToolTipText("Bearbeiten");
            deleteButton.setToolTipText("Löschen");

            add(viewButton);
            add(copyButton);
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    // Button Editor für die Aktions-Spalte
    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewButton = new JButton("👁");
        private JButton copyButton = new JButton("📋");
        private JButton editButton = new JButton("✏");
        private JButton deleteButton = new JButton("🗑");
        private int currentRow;

        public ButtonEditor() {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));

            viewButton.setToolTipText("Passwort anzeigen");
            copyButton.setToolTipText("Passwort kopieren");
            editButton.setToolTipText("Bearbeiten");
            deleteButton.setToolTipText("Löschen");

            panel.add(viewButton);
            panel.add(copyButton);
            panel.add(editButton);
            panel.add(deleteButton);

            viewButton.addActionListener(e -> viewPassword());
            copyButton.addActionListener(e -> copyPassword());
            editButton.addActionListener(e -> editPassword());
            deleteButton.addActionListener(e -> deletePassword());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        private void viewPassword() {
            List<PasswordEntry> passwords = vaultManager.getAllPasswords();
            if (currentRow >= 0 && currentRow < passwords.size()) {
                PasswordEntry entry = passwords.get(currentRow);

                String message = String.format("""
                    Titel: %s
                    Benutzername: %s
                    Passwort: %s
                    Website: %s
                    Erstellt: %s
                    """,
                        entry.getTitle(),
                        entry.getUsername(),
                        entry.getPassword(),
                        entry.getWebsite(),
                        formatDate(entry.getCreated())
                );

                JOptionPane.showMessageDialog(PasswordManager.this, message, "Passwort Details", JOptionPane.INFORMATION_MESSAGE);
            }
            fireEditingStopped();
        }

        private void copyPassword() {
            List<PasswordEntry> passwords = vaultManager.getAllPasswords();
            if (currentRow >= 0 && currentRow < passwords.size()) {
                PasswordEntry entry = passwords.get(currentRow);
                StringSelection selection = new StringSelection(entry.getPassword());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                showInfo("Passwort in Zwischenablage kopiert!");
            }
            fireEditingStopped();
        }

        private void editPassword() {
            List<PasswordEntry> passwords = vaultManager.getAllPasswords();
            if (currentRow >= 0 && currentRow < passwords.size()) {
                PasswordEntry entry = passwords.get(currentRow);

                AddPasswordDialog dialog = new AddPasswordDialog(PasswordManager.this, entry);
                PasswordEntry updatedEntry = dialog.showDialog();

                if (updatedEntry != null) {
                    if (vaultManager.updatePassword(currentRow, updatedEntry)) {
                        updatePasswordTable();
                        updateStatusBar();
                        showInfo("Passwort erfolgreich aktualisiert!");
                    } else {
                        showError("Fehler beim Aktualisieren des Passworts!");
                    }
                }
            }
            fireEditingStopped();
        }

        private void deletePassword() {
            List<PasswordEntry> passwords = vaultManager.getAllPasswords();
            if (currentRow >= 0 && currentRow < passwords.size()) {
                PasswordEntry entry = passwords.get(currentRow);

                int option = JOptionPane.showConfirmDialog(
                        PasswordManager.this,
                        "Sind Sie sicher, dass Sie das Passwort '" + entry.getTitle() + "' löschen möchten?",
                        "Passwort löschen",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    if (vaultManager.removePassword(currentRow)) {
                        updatePasswordTable();
                        updateStatusBar();
                        showInfo("Passwort erfolgreich gelöscht!");
                    } else {
                        showError("Fehler beim Löschen des Passworts!");
                    }
                }
            }
            fireEditingStopped();
        }

        @Override
        public Object getCellEditorValue() {
            return "Aktionen";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Setze System Look & Feel für native Optik
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                     InstantiationException | IllegalAccessException e) {
                // Verwende Standard Look & Feel wenn System L&F fehlschlägt
                System.err.println("Could not set system look and feel: " + e.getMessage());
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    System.err.println("Could not set cross-platform look and feel: " + ex.getMessage());
                }
            }

            new PasswordManager().setVisible(true);
        });
    }
}