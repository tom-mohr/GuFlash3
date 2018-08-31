Änderungen vom 31.08.18 19:20 | 1.11.1
- menu icons sollten jetzt funktionieren
- alle alten Gruppen wurden gelöscht
- man kann sich unter "abmelden" tatsächlich von Firebase-Auth. abmelden

=== bitte hier 1.11.0 einfügen ===


Änderungen vom 30.08.18 13:30 | 1.10
- User Klasse hat Zugriff auf den Usernamen, Email und soll aktive Gruppen speichern
- ProfileActivity hinzugefügt, Daten können dort verändert werden (aktive Gruppen sollen dort angezeigt werden)
- vorzeitige Navigationsleiste, die zum Profil und Gruppenscreen führt (wird anders umgesetzt)
- Chat nutzt jetzt den Usernamen statt der Email
- Firebase speichert nun Userdaten, außer dem Passwort, diese werden bei der Registrierung initialisiert
- extra Ordner für Activities


Änderungen vom 27.08.18 18:30 | 1.09
- Child "Gruppen" in Firebase heißt jetzt "groups"
- Gruppen haben nicht mehr ihren Namen als Key, sondern eine spezifische ID
- Wir verwenden jetzt Firebase "push()":
  -> Neue Gruppen und Nachrichten erhalten beim erstellen eine spezifische und chronologische ID
  -> warum das besser ist: https://www.firebase.com/docs/web/guide/saving-data.html#section-push
  -> So sind mehrere Gruppen mit gleichem Namen möglich (-> finde ich gut)
- die ChatActivity hat eine Action Bar
  -> Der Titel entspricht dem Gruppennamen
  -> Es gibt einen zurück-Button links oben, der in die GroupActivity umleitet
     (Das wurde nochmal im Manifest explizit festgelegt)
 - Die GroupActivity wurde hübscher gemacht


Änderungen vom 27.08.18 14:30 | 1.08
-GroupActivity hinzugefügt
-GroupActivity ist der eigentliche Home-Screen zu dem man nach dem login gebracht wird
-Es gibt jetzt mehrere Chaträume
-Es wurde ein zweiter RecyclerViewAdapter für die Gruppen hinzugefügt
-Gruppenscreen lässt sich nur durch refresh button aktualisieren


Änderungen vom 26.08.18 19:30
- BoringActivity heißt jetzt ChatActivity
- Chat-Design verbessert (benutzt jetzt RecyclerView)
- "Chat löschen"-Button entfernt
- Eine Leiste wird über dem Chat angezeigt
  -> Später wird hier mal der Event-Name angezeigt

Änderungen vom 26.08.18 18:30
- viele Log.d/w-Statements hinzugefügt oder verändert
- In den "Daten" der App wird der FirebaseUser gespeichert;
  Man bleibt also angemeldet, selbst wenn man das Handy aus- und wieder anschaltet
- MainActivity hat keine Buttons mehr.
  Sie hat die einzige Funktion, den User etweder zur Chat-Activtiy oder zur Signup-Activity umzuleiten,
  je nachdem, ob der User bereits am Gerät angemeldet ist, oder nicht.
  -> Man sieht also die MainActivity maximal GANZ kurz (ich sehe sie nicht).

- Das "users" Child in der Database ist jetzt nutzlos, da nur noch FirebaseUser verwendet wird.
  Sämtlicher Code, der damit zu tun hatte, wurde entfernt (außer die Klasse FirebaseMethods - wird aber nicht verwendet)
- Die Methoden von FirebaseMethods werden nicht verwendet


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
