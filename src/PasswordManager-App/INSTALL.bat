@echo off 
title Passwort-Manager Installation 
echo. 
echo ████████████████████████████████████████ 
echo █     PASSWORT-MANAGER INSTALLATION     █ 
echo ████████████████████████████████████████ 
echo. 
echo [1/3] Erstelle Desktop-Verknüpfung... 
powershell -Command "$s=(New-Object -COM WScript.Shell).CreateShortcut('%userprofile%\Desktop\Passwort-Manager.lnk'); $s.TargetPath='%cd%\PasswordManager.vbs'; $s.WorkingDirectory='%cd%'; $s.Description='Sicherer Passwort-Manager'; if(Test-Path '%cd%\icons\app-icon.ico'){$s.IconLocation='%cd%\icons\app-icon.ico'}; $s.Save()" 
echo ✅ Desktop-Verknüpfung erstellt 
echo. 
echo [2/3] Erstelle Startmenü-Eintrag... 
powershell -Command "$s=(New-Object -COM WScript.Shell).CreateShortcut('%appdata%\Microsoft\Windows\Start Menu\Programs\Passwort-Manager.lnk'); $s.TargetPath='%cd%\PasswordManager.vbs'; $s.WorkingDirectory='%cd%'; $s.Description='Sicherer Passwort-Manager'; if(Test-Path '%cd%\icons\app-icon.ico'){$s.IconLocation='%cd%\icons\app-icon.ico'}; $s.Save()" 
echo ✅ Startmenü-Eintrag erstellt 
echo. 
echo [3/3] Registriere App... 
echo ✅ Installation abgeschlossen! 
echo. 
echo ████████████████████████████████████████ 
echo █              VERWENDUNG               █ 
echo ████████████████████████████████████████ 
echo. 
echo 🖥️  Desktop: Doppelklick "Passwort-Manager" 
echo 🔍 Start: Suche "Passwort-Manager" 
echo 📁 Direkt: Doppelklick "PasswordManager.vbs" 
echo. 
echo ⚠️  Ordner nicht verschieben! 
echo. 
pause 
