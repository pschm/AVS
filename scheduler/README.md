# Scheduler
Der Scheduler bildet die Schnittstelle zwischen der Unity-Instanz und den einzelnen Workern ab. Über eine REST-API (siehe Scheduler Ressourcentabelle) können sich u. a. die Worker anmelden und die Daten aktualisieren.

<!-- ## Funktionen -->


## Installation und Ausführung
Da es sich bei der Umsetzung des Schedulers um ein Gradle-Projekt handelt, ist es notwendig das die dafür genutzte IDE (z.B. Intellij) dies idealerweise supportet. Sollte Intellij das Projekt nicht automatisch erkennen, muss die Gradle-Synchronisation manuell gestartet werden. Dies ist über das Öffnen  der Datei "build.gradle" und betätigen des "Import all gradle projects" möglich.

Sollten Fehler auftreten, kann auch eine ausführbare .exe Datei zur Verfügung gestellt werden.


### Scheduler Ressourcentabelle
|Ressource  |Verb |URI |Semantik |Contenttype-Request | Contenttype-Response |
|-----------|-----|----|---------|--------------------|----------------------|
|Worker||||
||Post|/worker|Erstellt einen Worker|application/json ||
||Put |/worker|Akutalisiert den Worker|applicaiton/json ||
|Map||||
||Get|/map|Liefert die Karte der Unity Instanz ||application/json|
||Post|/map|Setzt die Karte der Unity Instanz |application/json||
|Path||||
||Get|/path|Liefert den aktuell besten Weg zurück. ||application/json|

*Hinweis: Die Identifizierung der Worker geschieht momentan über die IP-Adresse. Zukünftig wird beim POST auf "/worker" eine UUID erzeugt und im JSON zurückgegeben. Diese muss dann bei einem PUT als Parameter (id) mitgeliefert werden. [Implementierung vorraussichtlich Dienstag]*
