# Scheduler
Der Scheduler bildet die Schnittstelle zwischen der Unity-Instanz und den einzelnen Workern ab. Über eine REST-API (siehe Scheduler Ressourcentabelle) können sich u. a. die Worker anmelden und die Daten aktualisieren.

<!-- ## Funktionen -->


## Installation und Ausführung
Da es sich bei der Umsetzung des Schedulers um ein Gradle-Projekt handelt, ist es notwendig das die dafür genutzte IDE (z.B. Intellij) dies idealerweise supportet. Sollte Intellij das Projekt nicht automatisch erkennen, muss die Gradle-Synchronisation manuell gestartet werden. Dies ist über das Öffnen  der Datei "build.gradle" und betätigen des "Import all gradle projects" möglich. Wird anschließend der "Run"-Button (Umschalt+F10) nicht direkt grün und somit ausführbar, muss die main.kt geöffnet und dort der "Run"-Button betätigt werden.

Der Server läuft dann auf localhost mit dem Port 8080. 

Sollten Fehler auftreten, kann auch eine ausführbare .exe Datei zur Verfügung gestellt werden.


### Scheduler Ressourcentabelle
|Ressource  |Verb |URI |Semantik |Contenttype-Request | Contenttype-Response |
|-----------|-----|----|---------|--------------------|----------------------|
|Worker||||
||Post|/worker|Erstellt einen Worker|application/json ||
||Put |/worker?uuid={parameter}|Akutalisiert den entsprechenden Worker|applicaiton/json ||
|Map||||
||Get|/map|Liefert die Karte der Unity Instanz ||application/json|
||Post|/map|Setzt die Karte der Unity Instanz |application/json||
|Path||||
||Get|/path|Liefert den aktuell besten Weg zurück. ||application/json|


