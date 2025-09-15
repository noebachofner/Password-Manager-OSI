# 🔐 Sicherer Passwort-Manager

Ein vollständig in Java entwickelter Passwort-Manager mit modernster AES-256 Verschlüsselung und benutzerfreundlicher Swing-GUI.

## ✨ Features

### 🔒 **Sicherheit**
- **AES-256 Verschlüsselung** - Militärgrad-Sicherheit für alle Passwörter
- **Master-Passwort Authentifizierung** - Sichere Zugriffskontrolle
- **Verschlüsselte .vault Dateien** - Lokale Speicherung ohne Cloud-Risiken
- **Sichere Schlüssel-Ableitung** - SHA-256 Hash-basierte Schlüsselgenerierung

### 🎲 **Passwort-Generierung**
- **Anpassbare Generierung** - Groß-/Kleinbuchstaben, Zahlen, Sonderzeichen
- **Variable Länge** - 4 bis 128 Zeichen
- **Passwort-Stärke Analyse** - Echtzeit-Bewertung der Sicherheit
- **Schnellgenerierung** - Ein-Klick Standard-Passwörter

### 🖥️ **Benutzeroberfläche**
- **Moderne Java Swing GUI** - Native Systemintegration
- **Übersichtliche Tabelle** - Alle Passwörter auf einen Blick
- **Intelligente Suche** - Schnelles Finden von Einträgen
- **Tastatur-Shortcuts** - Effiziente Bedienung

### 🤖 **Automation**
- **Passwort-Feld Erkennung** - Automatische Detektion (simuliert)
- **Ein-Klick Einfügen** - Automatisches Ausfüllen von Formularen
- **Smart-Speicherung** - Intelligente Erfassung von Website-Daten

### 📁 **Dateiverwaltung**
- **Mehrere Tresor-Dateien** - Getrennte Passwort-Sammlungen
- **Automatisches Speichern** - Keine Datenverluste
- **Portable Dateien** - .vault Dateien sind übertragbar

## 🚀 Installation & Start

### Voraussetzungen
- **Java JDK 8 oder höher** - [Download hier](https://www.oracle.com/java/technologies/downloads/)

### Schnellstart (Windows)
1. **Alle Dateien herunterladen** und in einen Ordner entpacken
2. **Doppelklick auf `run.bat`** - Kompiliert und startet automatisch
3. **Fertig!** Der Passwort-Manager öffnet sich

### Manuelle Kompilierung
```bash
# Kompilieren
javac *.java

# Starten
java PasswordManager
```

### Linux/Mac
```bash
# Berechtigung setzen
chmod +x compile.sh run.sh

# Kompilieren und starten
./run.sh
```

## 📖 Verwendung

### 🔐 **Erster Start**
1. **"Neuen Tresor erstellen"** wählen
2. **Speicherort auswählen** (z.B. `Meine_Passwörter.vault`)
3. **Master-Passwort festlegen** (gut merken!)
4. **Tresor wird erstellt** und geöffnet

### ➕ **Passwort hinzufügen**
1. **"Neues Passwort"** Button klicken
2. **Titel eingeben** (z.B. "Gmail")
3. **Benutzername eingeben**
4. **Passwort eingeben** oder **🎲 generieren lassen**
5. **Website eingeben** (wird automatisch vervollständigt)
6. **"Speichern"** klicken

### 🔍 **Passwörter verwalten**
- **Suchen**: Suchfeld nutzen (durchsucht Titel, Benutzername, Website)
- **Anzeigen**: 👁️ Button zum Passwort anzeigen
- **Kopieren**: 📋 Button kopiert Passwort in Zwischenablage
- **Bearbeiten**: ✏️ Button zum Ändern der Daten
- **Löschen**: 🗑️ Button entfernt Eintrag (mit Bestätigung)

### 🎲 **Passwort-Generator**
1. **"Generator"** Button oder **Ctrl+G** drücken
2. **Optionen auswählen**:
    - ✅ Großbuchstaben (A-Z)
    - ✅ Kleinbuchstaben (a-z)
    - ✅ Zahlen (0-9)
    - ✅ Sonderzeichen (!@#$...)
3. **Länge einstellen** (4-128 Zeichen)
4. **"Generieren"** klicken
5. **Passwort kopieren oder verwenden**

### 🤖 **Automatische Feld-Erkennung**
1. **"Passwort-Felder prüfen"** im Tools-Menü
2. **Simulation** einer Passwort-Feld-Erkennung
3. **Automatische Generierung und Speicherung**

## 📁 Projektstruktur

```
PasswordManager/
├── PasswordEntry.java              # Datenmodell für Passwort-Einträge
├── EncryptionManager.java          # AES-256 Verschlüsselungslogik
├── PasswordGenerator.java          # Sichere Passwort-Generierung
├── VaultManager.java               # Datei-Operationen und Tresor-Verwaltung
├── LoginDialog.java                # Anmelde-Dialog für Tresor-Zugang
├── PasswordGeneratorDialog.java    # GUI für Passwort-Generierung
├── AddPasswordDialog.java          # Dialog zum Hinzufügen/Bearbeiten
├── PasswordManager.java            # Hauptanwendung und GUI-Koordination
├── compile.bat                     # Windows Kompilier-Skript
├── run.bat                         # Windows Start-Skript
└── README.md                       # Diese Dokumentation
```

## 🔧 Technische Details

### Verschlüsselung
- **Algorithmus**: AES-256-CBC
- **Schlüssel-Ableitung**: SHA-256 Hash des Master-Passworts
- **IV**: Zufällig generiert für jeden Verschlüsselungsvorgang
- **Padding**: PKCS5Padding für sichere Blockchiffrierung

### Dateien
- **Format**: `.vault` Dateien (Base64-kodierte verschlüsselte Daten)
- **Struktur**: `IV + Verschlüsselte Daten` kombiniert und Base64-kodiert
- **Serialisierung**: Pipe-getrennte Werte (`|`) mit Escaping

### Sicherheitsmerkmale
- **Keine Klartext-Speicherung** - Alle Daten verschlüsselt
- **Speicher-Sicherheit** - Passwörter werden nach Gebrauch gelöscht
- **Sichere Zufallsgenerierung** - `SecureRandom` für Kryptografie
- **Session-Management** - Automatische Abmeldung möglich

## ⌨️ Tastatur-Shortcuts

| Shortcut | Aktion |
|----------|--------|
| `Ctrl+N` | Neuer Tresor |
| `Ctrl+O` | Tresor öffnen |
| `Ctrl+S` | Tresor speichern |
| `Ctrl+Plus` | Neues Passwort |
| `Ctrl+G` | Passwort-Generator |
| `Ctrl+Q` | Beenden |

## 🛡️ Sicherheitsempfehlungen

### Master-Passwort
- **Mindestens 12 Zeichen** lang
- **Kombination** aus Buchstaben, Zahlen und Sonderzeichen
- **Nicht anderweitig verwendet**
- **Gut merkbar** aber schwer zu erraten

### Tresor-Dateien
- **Regelmäßige Backups** erstellen
- **Sichere Speicherorte** wählen (verschlüsselte Laufwerke)
- **Nicht in Cloud-Syncs** ohne zusätzliche Verschlüsselung

### Allgemeine Nutzung
- **Tresor nach Gebrauch schließen**
- **Bildschirm sperren** bei Verlassen des Arbeitsplatzes
- **Software aktuell halten**

## 🔄 Zukünftige Features

- 🌐 **Browser-Integration** - Automatisches Ausfüllen in echten Browsern
- 📱 **Mobile Companion App** - Synchronisation mit Smartphone
- 🔄 **Import/Export** - CSV, KeePass, LastPass Kompatibilität
- 👥 **Geteilte Tresore** - Familien- oder Team-Passwörter
- 🔔 **Passwort-Ablauf** - Erinnerungen für Passwort-Updates
- 📊 **Sicherheits-Dashboard** - Übersicht schwacher Passwörter
- 🔒 **2FA Integration** - TOTP Code Generierung
- ☁️ **Sichere Cloud-Sync** - Ende-zu-Ende verschlüsselt

## 🐛 Fehlerbehebung

### "Java nicht gefunden"
- Java JDK installieren und in PATH eintragen
- `java -version` in Kommandozeile testen

### "Tresor kann nicht gelöffnet werden"
- Master-Passwort korrekt eingeben
- Tresor-Datei nicht beschädigt/verschoben
- Ausreichende Dateiberechtigungen

### "Kompilierungsfehler"
- Alle `.java` Dateien im gleichen Verzeichnis
- Java JDK (nicht nur JRE) installiert
- Keine Sonderzeichen im Dateipfad

## 📄 Lizenz

Dieses Projekt ist Open Source und steht unter der MIT-Lizenz zur Verfügung.

## 🤝 Beitragen

Verbesserungen und Bugfixes sind willkommen! Einfach einen Pull Request erstellen.

---

**Entwickelt mit ❤️ und ☕ für maximale Passwort-Sicherheit!**