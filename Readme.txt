Änderungen vom 13.09.18 | 1.18.0 (G)
-Es gibt keine Gruppen mehr sondern nur Events (in der Datenbank heißen die events immer noch Gruppen)
-Meine Events Activity hinzugefügt
    -wenn man auf den Button join klickt wird die gruppe dem User hinzugefügt und er kann sie unter meine Gruppen sehen
-wenn man sich im Chat einer Gruppe befindet und noch nicht als User eingetragen ist, wird man als User hinzugefügt     (das könnte man auch zum join button packen, aber dafür muss man auf die Teilnehmerliste zugreifen ohne das man Teilnehmer ist; wollen wir das?)
-Teilnehmerzahl wird jetzt angezeigt
-man muss eine maximale Teilnehmerzahl eingeben, dafür wird es richtig angezeigt und es wird die Differenz zwischen max und momentan als Text ausgeben(min Teilnehmerzahl fehlt noch)


Änderungen vom 02.09.18 | 1.17.0 (T)
- Chat-Design verbessert
- Chat REVOLUTIONIERT:
  - zwei unterschiedliche Layouts für eigene und fremde Nachrichten
    -> RecyclerViewAdapter für Chat nochmal völlig überarbeitet
  - Timestamps
    -> Die Zeit wird in der Datenbank einfach in einem einzigen long gespeichert.
       Daraus kann man, wenn man will, später Datum und Uhrzeit wieder herstellen,
       und angepasst an die lokalen Gewohnheiten formatieren.
       Das wirkt vllt lame, hat aber ganz schön lange gedauert das herauszufinden xD
  - anstatt Username wird jetzt die UserID an jede Message gebunden
    -> Für den Fall dass sich der Username ändert
       ABER: Bis jetzt werden einfach nur die UserIDs anstatt des Usernames angezeigt
             ( -> Geplant ist, dass die mit den UserIDs verknüpften Usernames
                  beim Laden der Gruppe von der Datenbank abgefragt werden. )
  => Ich musste also die Message-Klasse ändern und alle Gruppen löschen.


Änderungen vom 02.09.18 | 1.16.1 (T)
- Chat-Design verbessert

Änderungen vom 02.09.18 | 1.16.0 (T)
- Chat-Design verbessert

Änderungen vom 02.09.18 | 1.15.1 (T)
- settings icon nachgereicht

Änderungen vom 02.09.18 | 1.15.0 (T)
- bugfix: ConfigurationActivity in Android Manifest deklariert
- NavigationActivity ist jetzt launchmode="singleTop" (im Manifest),
  damit sie nicht jedesmal restarted wird, wenn man in einer anderen
  Activity den back button in der Action Bar drückt (geht schneller, vermeidet bugs)
  -> https://stackoverflow.com/questions/21805382/how-to-resume-activity-instead-of-restart-when-going-up-from-action-bar
  -> https://stackoverflow.com/questions/14462456/returning-from-an-activity-using-navigateupfromsametask/16147110#16147110
- Beim Abmelden wird ein Bestätigungsdialog angezeigt
- Menu icons und app icon verbessert und dateien "aufgeräumt"
- Layout von Event items

Änderungen vom 01.09.18 23:00 | 1.14.0 (G)
-kleine Bugfixes für besseres Erlebnis
-Gruppenbeschreibung wird nun hinzugefügt (RecyclerViewAdapter braucht nur noch ID-ArrayList)
-Platzhalter für Teilnehmeranzahl
-ConfigurationActivity hinzugefügt die noch keine Funktionen hat

-Textdatei für Bugs und ToDOs für bessere Übersichtlichkeit

Änderungen vom 01.09.18 15:30 | 1.13.0 (T)
- "Event erstellen"-Activity designt.
  -> PlacePicker verwendet
     -> musste App mit Google Places API verbinden

Änderungen vom 01.09.18 1:30 | 1.12.0 (T)
- alle alten user (auch in firebase-auth.! -> neu registrieren) und groups wurden gelöscht
- Gruppen/"groups" heissen jetzt "events"
- User-class verbessert und verändert
  -> vor allem: setter verändern immer zwei sachen:
     1. den wert von der privaten variable des user objekts
     2. den eintrag in der datenbank
- Profil-Activity hübscher gemacht
- Username ist bearbeitbar
- Passwort ist neu wählbar -> epische eingabefelder, die sich durch button aktivieren lassen

Änderungen vom 31.08.18 19:20 | 1.11.1 (T)
- menu icons sollten jetzt funktionieren
- alle alten Gruppen wurden gelöscht
- man kann sich unter "abmelden" tatsächlich von Firebase-Auth. abmelden

=== 1.11.0 (T) Log fehlt ===


Änderungen vom 30.08.18 13:30 | 1.10 (G)
- User Klasse hat Zugriff auf den Usernamen, Email und soll aktive Gruppen speichern
- ProfileActivity hinzugefügt, Daten können dort verändert werden (aktive Gruppen sollen dort angezeigt werden)
- vorzeitige Navigationsleiste, die zum Profil und Gruppenscreen führt (wird anders umgesetzt)
- Chat nutzt jetzt den Usernamen statt der Email
- Firebase speichert nun Userdaten, außer dem Passwort, diese werden bei der Registrierung initialisiert
- extra Ordner für Activities


Änderungen vom 27.08.18 18:30 | 1.09 (T)
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