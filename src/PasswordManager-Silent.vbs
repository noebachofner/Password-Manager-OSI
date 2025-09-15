' ========================================
' Passwort-Manager - Unsichtbarer Starter
' ========================================

On Error Resume Next

' Aktuelles Verzeichnis ermitteln
Dim fso, currentDir
Set fso = CreateObject("Scripting.FileSystemObject")
currentDir = fso.GetParentFolderName(WScript.ScriptFullName)

' Java-Pfade definieren (automatische Erkennung)
Dim javaPath, jarPath
javaPath = FindJava()
jarPath = currentDir & "\lib\PasswordManager.jar"

' Prüfe ob JAR-Datei existiert
If Not fso.FileExists(jarPath) Then
    ' Fallback: Verwende .class Dateien
    jarPath = currentDir & "\lib"
    RunJavaClass currentDir, javaPath
Else
    ' Verwende JAR-Datei
    RunJavaJar jarPath, javaPath
End If

' Java-Installation automatisch finden
Function FindJava()
    Dim paths, i, testPath
    paths = Array( _
        "C:\Program Files\Java\jdk-21\bin\javaw.exe", _
        "C:\Program Files\Java\jdk-17\bin\javaw.exe", _
        "C:\Program Files\Java\jdk-11\bin\javaw.exe", _
        "C:\Program Files\Java\jdk-8\bin\javaw.exe", _
        "C:\Program Files (x86)\Java\jdk-21\bin\javaw.exe", _
        "C:\Program Files (x86)\Java\jdk-17\bin\javaw.exe", _
        "C:\Program Files (x86)\Java\jdk-11\bin\javaw.exe", _
        "C:\Program Files (x86)\Java\jdk-8\bin\javaw.exe", _
        "javaw.exe" _
    )

    ' Durchsuche Standard-Java-Installationen
    For Each testPath In paths
        If fso.FileExists(testPath) Or testPath = "javaw.exe" Then
            FindJava = testPath
            Exit Function
        End If
    Next

    ' Fallback: Durchsuche Java-Ordner dynamisch
    FindJava = SearchJavaFolders()
End Function

' Durchsuche Java-Ordner nach javaw.exe
Function SearchJavaFolders()
    Dim folder, subfolder, javaPaths, path
    javaPaths = Array("C:\Program Files\Java", "C:\Program Files (x86)\Java")

    For Each path In javaPaths
        If fso.FolderExists(path) Then
            Set folder = fso.GetFolder(path)
            For Each subfolder In folder.SubFolders
                Dim javaExe
                javaExe = subfolder.Path & "\bin\javaw.exe"
                If fso.FileExists(javaExe) Then
                    SearchJavaFolders = javaExe
                    Exit Function
                End If
            Next
        End If
    Next

    ' Letzter Fallback
    SearchJavaFolders = "javaw.exe"
End Function

' Java JAR-Datei ausführen
Sub RunJavaJar(jarFile, javaExe)
    Dim shell, command
    Set shell = CreateObject("WScript.Shell")

    command = """" & javaExe & """ -jar """ & jarFile & """"

    ' Unsichtbar ausführen (WindowStyle = 0)
    shell.Run command, 0, False
End Sub

' Java .class Dateien ausführen
Sub RunJavaClass(classPath, javaExe)
    Dim shell, command
    Set shell = CreateObject("WScript.Shell")

    command = """" & javaExe & """ -cp """ & classPath & "\lib"" PasswordManager"

    ' Unsichtbar ausführen (WindowStyle = 0)
    shell.Run command, 0, False
End Sub