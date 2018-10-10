Änderungen vom 10.10.18 | 1.34.1 (G)
-Navigation behält die Sortier-/Filteroptionen beim drehen und öffnen anderer Activities

Änderungen vom 07.10.18 | 1.34.0 (G)
- Eventersteller wird automatisch zum Event hinzugefügt
- CreateEvent behält Daten, selbst wenn man den Bildschirm rotiert
- Navigation liest nur sortType aus savedInstance richtig aus

Änderungen vom 07.10.18 | 1.33.0 (T)
- Im Side-Drawer ist "Alle Events" am Anfang SICHTBAR ausgewählt
- GUI-Feedback:
  Wenn "Meine Events" aktiviert wird: Ändere Titel zu "Meine Events"
  Wenn "Alle Events" aktiviert wird: Ändere Titel zu "Events"
- MyEventActivity gelöscht
- Standort verbessert: Auch wenn der User erst nachträglich GPS aktiviert,
  werden die Entfernungen noch berechnet (nach spätestens 1 min)
  -> ist aber immer noch buggy... Funktioniert manchmal gar nicht :(

Änderungen vom 07.10.18 | 1.32.0 (G)
- getDeltaTimeInfoString grammatikalisch angepasst und es wird ein Datum angezeigt
- MyEventActivity ist useless geworden, beim klicken auf meine Events wird ein weiterer Filter aktiviert
- EventInformationActivity hinzugefügt, die bei dem Chat und ClosedChat geöffnet werden soll
- ClosedChat passt fehlende Teilnehmerzahl an und leitet an den Chat weiter

Änderungen vom 06.10.18 | 1.31.0 (T)
- Standort eingeführt:
  Die NavigationActivity fragt beim Start genau einmal den Standort des Geräts ab und merkt ihn sich.
  Dann wird das Attribut "distance" von jeder EventInfo neu berechnet und die Liste neu sortiert.
- die Häkchen im Filtermenü funktionieren jetzt ("nicht weiter / später als ...")
- Alphabetische Sortierung eingeführt
- Ein Feld unter der ActionBar zeigt die aktiven Filteroptionen an
- Menü-Icons verändert
- Item-Divider bei der Event-Liste (RecyclerView) hinzugefügt
- Simplicity: G's Funktion zur Teilnehmerzahl noch mehr verkürzt,
  wird in jedem Listen-Item unter dem Event-Namen angezeigt
- Neue Farb-Palette für die App (die alte hat genervt) und viele kleine Verbesserungen am Design
- Layout vom EditText für Event-Beschreibung verbessert
- Chat: RecyclerViewAdapter scrollt automatisch nach unten, sobald eine neue Nachricht geladen wird
  (also auch beim Öffnen den Chats)

Änderungen vom 06.10.18 | 1.30.0 (T)
- Info-String zur Teilnehmerzahl des Events lassen sich über eine Methode des EventInfo-Strings generieren
  (einfach den Code von EventRecyclerViewAdapter ausgelagert)
- Sortierung wird jetzt fast ausschließlich vom EventRecyclerViewAdapter verwaltet
  -> es wird keine neue Activity gestartet wie bisher,
     sondern einfach nur der Inhalt der Liste geändert
- Design für die Elemente der Event-Liste erweitert:
  -> Zeit bis zum Stattfinden wird angezeigt
- Bugfix bei EventInfo.getMillisTillEvent
  -> funktioniert jetzt wirklich
- Zeit-Icon geändert
- Die Event-Liste (RecyclerView) hat jetzt unten ein Padding, sodass der "Floating Action Button" (FAB)
  nicht mehr das letzte Element verdecken kann.
- Simplicity: FAB-Icon ersetzt durch einfaches Plus

Änderungen vom 04.10.18 | 1.29.0 (G)
-Unterschiedlicher Differenzinfo je nach Teilnehmerzahl erreicht wurde (farbliche Kodierung?)
-MyEventActivity gefixt
-Wenn man auf sortiere nach Zeit drückt, wird die Activity neu geladen und richtig sortiert


Änderungen vom 04.10.18 | 1.28.1 (T)
- winziger Bugfix, nicht der Rede wert...

Änderungen vom 04.10.18 | 1.28.0 (T)
- Bugfix: recyclerviewadapterEvent aktualisiert nicht, wenn ein neues Event geadded wurde
  -> DAS HIER WAR MEGA DER BUG.. hab 3 stunden bebraucht:
     Problem war folgendes:
       Wenn man ein neues event in der datenbank added, setzt man ja nacheinander die
       verschiedenen attribute (name, beschreibung, ...).
       Sobald man aber das erste attribut setzt, wird schon bei dem ChildEventListener die
       onChildAdded-Methode getriggered. Zu dem Zeitpunkt sind aber die anderen attribute noch
       gar nicht in der Datenbank. Dadurch hat der DataSnapshot nur ein einziges Child ("name").
     Lösung:
       Nachdem alle attribute hinzugefügt wurden, wir ein letztes attribut geadded, mit dem namen
       "READY" -> erst wenn dieses READY-child da ist, darf es verarbeitet werden
- alte Klassen "Event" und "EventLocation" entfernt
- neue Klasse "EventInfo" -> einfacher alle informationen zu einem event übergeben
- Code in NaviationActivity und EventRecyclerViewAdapter teilweise völlig neu geschrieben
- Die MyEventActivity ist vorübergehend nutzlos (die neuen Änderungen haben zu errors geführt)
- Bugifx / zukünftige Errors vermeiden:
  Wenn in der Datenbank nicht alle Zeit-Daten zum Event gegeben sind: ignoriere es
- Bugfix beim abbrechen der Zeitauswahl
- RecyclerViewAdapterEvent umbenannt in EventRecyclerViewAdapter -> verständlicher
- code für substring bei langem event-namen entfernt
  -> hätte in dieser form zu error führen können
  -> sollte später aber wieder eingeführt werden
- Überschrift beim "abmelden"-Dialog wird jetzt auch kleingeschrieben

Änderungen vom 03.10.18 | 1.27.0 (T)
- Create Event:
  - Funktion "checkTime" von G vereinfacht
  - Überschrift "Teilnehmer" zu "Wie viele?" geändert
    -> konsequent mit den Überschriften als Fragen
    -> Man könnte sonst meinen, das Alter der Teilnehmer sei gemeint
  - Mindestanzahl von Teilnehmern muss mindestens 3 betragen
  - Höchstzahl muss größer/gleich Mindestanzahl sein
- alte events werden noch angezeigt, wenn sie weniger als 12 stunden
  in der vergangenheit liegen
- farben in navigation activity und popup geändert -> bessere lesbarkeit
- Filter-Menü hinzugefügt, aber noch keine Filter-Funktionen implementiert

Änderungen vom 02.10.18 | 1.26.0 (G)
-ClosedChatActivity hinzugefügt, die geöffnet wird, wenn die Mindestteilnehmerzahl nicht erreicht wurde(manchmal buggy)
-Events die in der Vergangenheit liegen werden nicht mehr angezeigt und können nicht erstellt werden
-Änderungsvorschläge übernommen

Änderungen vom 02.10.18 | 1.25.0 (T)
- man ist beim Event-Erstellen gezwungen, Datum und Uhrzeit anzugeben
  -> es gibt nur noch einen Button "Zeit wählen",
     der beide Dialoge (datum und uhrzeit) hintereinander aufruft
- Formatierung der Zeit vereinfacht
  -> benutzt android.text.format.DateFormat
  -> passt sich den lokalen standards an
- Popup-Design verbessert

Änderungen vom 01.10.18 | 1.24.1 (G)
-Zeit und Ort wird in der Eventinfo ausgegeben und kleiner Bugfix, da Name doppelt überschrieben wurde


Änderungen vom 01.10.18 | 1.24.0 (T)
- createEventActivity:
  - Layout verbessert (Icons bei headlines, Buttons etc.)
  - G's Activities für Time- und Date-Picking wieder entfernt (sorry :/)
    und durch Dialoge ersetzt -> ist viel einfacher so
  - wenn die eingaben fehlerhaft sind, kann keine gruppe erstellt werden
    und es wird ein individueller Hinweis angezeigt (Snackbar)
    -> es wird allerdings noch nicht alles gecheckt

Änderungen vom 30.09.18 | 1.23.0 (G)
-Zeit und Datum werden innerhalb von 2 Activities ausgewählt und dann auch gespeichert
-Anfänge zur Ortsspeicherung, aber kann es nicht testen

Änderungen vom 30.09.18 | 1.22.0 (T)
- G's Code aufgeräumt
- Teilnehmerzahlen werden im Popup angezeigt
- der join-button wird deaktiviert, sobald kein platz mehr in der gruppe ist

Änderungen vom 30.09.18 | 1.21.0 (G)
-Popup funktioniert jetzt richtig und User und Event werden beide im Popup hinzugefügt
    -Join Button beim Event-Layout entfernt


Änderungen vom 29.09.18 | 1.20.0 (G+T)
-Im Chat wird nun der Name statt der Id angezeigt
-Meine Events funktionieren wieder
-Bug bei Teilnehmerzahl gefixt


Änderungen vom 29.09.18 | 1.19.0 (T)
-Popup um zu joinen und für Eventinfos hinzugefügt
-Code aufgeräumt (Events ersetzt)
-Bug gefixt beim Erstellen einer Gruppe


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