Änderungen vom 26.08.18 13:30

-Abstürze wurden größtenteils gefixt, App ist jetzt wieder benutzbar
    !!!Bei Versuch einer zweiten Regestrierung stürzt das Programm wieder ab

-Regestrierung und Login sind jetzt möglich

-FirebaseMethods wurde verändert
    -UsernameExists führt zu keiner Endlosschleife
    -addUserToDatabase speichert jetzt unter der node "xxxxx" statt "xxxxx@xxx.xx"
    -username=email

-LoginActivity wurde nach dem selben Schema wie Signup Activity aufgebaut

-Activity_Boring.xml komisches Passwortfeld entfernt




Änderungen vom 25.08.18 13:30

-Readme hinzugefügt

-Klasse FirebaseMethods hinzugefügt
	-Funktion um User zur Datenbank hinzuzufügen
	-Funktion um bereits verwendeten Usernamen zu checken

-SignupActivity in Unterklassen aufgeteilt
	-setupFirebase beeinhaltet die Initialisierung von Firebase-Funktionen und
	 einen AuthStateListener der dafür sorgen soll das User hinzugefügt und weitergeleitet werden
	!stürzt wegen onStart() bei öffnen der Activity ab

	-init kümmert sich um die Initialisierung der Interface-Elemente und um die Email-
	 Verfizierung
	!stürzt bei Eingeben einer Email Adresse ab(Zeichenfolge funktioniert)

-User Klasse get und set Funktion hinzugefügt

-Activity_Login.xml LinearLayout hinzugefügt
