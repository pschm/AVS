# Scheduler
Der Scheduler bildet die Schnittstelle zwischen der Unity-Instanz und den einzelnen Workern ab. Über eine REST-API (siehe Scheduler Ressourcentabelle) können sich u. a. die Worker anmelden und die Daten aktualisieren.

<!-- ## Funktionen -->


## Installation und Ausführung
Da es sich bei der Umsetzung des Schedulers um ein Gradle-Projekt handelt, ist es notwendig das die dafür genutzte IDE (z.B. Intellij) dies idealerweise supportet. Sollte Intellij das Projekt nicht automatisch erkennen, muss die Gradle-Synchronisation manuell gestartet werden. Dies ist über das Öffnen  der Datei "build.gradle" und betätigen des "Import all gradle projects" möglich. Wird anschließend der "Run"-Button (Umschalt+F10) nicht direkt grün und somit ausführbar, muss die main.kt geöffnet und dort der "Run"-Button betätigt werden.

Der Server läuft dann auf localhost mit dem Port 8080. 


### Scheduler Ressourcentabelle
|Ressource  |Verb |URI |Semantik |Contenttype-Request | Contenttype-Response |
|-----------|-----|----|---------|--------------------|----------------------|
|Worker||||
||Post|/worker|Erstellt einen Worker und gibt UUID und Population zurück|application/json|application/json|
||Put |/worker?uuid={parameter}|Akutalisiert den entsprechenden Worker und liefert eine neue Population zurück|applicaiton/json |applicaiton/json|
|Map||||
||Get|/map|Liefert die Karte der Unity Instanz ||application/json|
||Post|/map|Setzt die Karte der Unity Instanz |application/json||
|Path||||
||Get|/path|Liefert den aktuell besten Weg zurück. ||application/json|

### Scheduler HTTP-Fehlercodes
| Ressource | Verb | Fehlercode |                     | Beschreibung                                                            |
|-----------|------|------------|---------------------|-------------------------------------------------------------------------|
| /worker   | POST | 503        | Service Unavailable | Die Map ist nicht gesetzt.                                              |
|           |      |            |                     | Die maximale Anzahl der Arbeiter wurde erreicht.                        |
|           | PUT  | 400        | Bad Request         | Population ist nicht valide (enthält z.B. null-Werte).                  |
|           |      |            |                     | Das JSON konnte nicht gelesen werden.                                   |
|           |      |            |                     | Die UUID fehlt.                                                         |
|           |      |            |                     | Der Nutzer ist nicht registriert.                                       |
| /map      | GET  | 204        | No Content          | Die Map ist nicht verfügbar.                                            |
|           | POST | 400        | Bad Request         | Das JSON konnte nicht gelesen werden.                                   |
| /path     | GET  | 503        | Service Unavailable | Es gibt momentan keine registrieren Worker, die an der Lösung arbeiten. |
|           |      | 204        | No Content          | Die Worker haben noch kein Ergebnis geliefert.
