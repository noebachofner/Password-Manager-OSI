' ========================================
' 🔐 Passwort-Manager - Einfacher Start
' ========================================
' Startet die App OHNE CMD-Fenster

Dim shell, fso, currentDir, javaCommand

' Erstelle WScript.Shell Objekt
Set shell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")

' Aktuelles Verzeichnis ermitteln
currentDir = fso.GetParentFolderName(WScript.ScriptFullName)

' Wechsle ins WindowsApp Verzeichnis falls vorhanden
If fso.FolderExists(currentDir & "\WindowsApp") Then
    currentDir = currentDir & "\WindowsApp"
End If

' Java-Befehl zusammenstellen
If fso.FileExists(currentDir & "\lib\PasswordManager.jar") Then
' JAR-Datei vorhanden
    javaCommand = "javaw -jar """ & currentDir & "\lib\PasswordManager.jar"""
Else
' Fallback: Class-Dateien
    javaCommand = "javaw -cp """ & currentDir & "\lib"" PasswordManager"
End If

' App UNSICHTBAR starten (0 = versteckt)
shell.Run javaCommand, 0, False

' VBScript beenden
WScript.Quit