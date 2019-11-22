# Worker
Worker steht ständig in Verbindung mit dem Scheduler, um neue Individuen abfragen zu können. Diese werden mittels Genetische Algorithmen und Travelling Salesman Problem berechnet. Zurzeit werden 100.000 Generationen verwendet. Dies dauert in der Regel 5 Sekunden.

<!-- ## Funktionen -->


## Installation und Ausführung
Es ist ein klassisches Java Programm, wo man nicht so viel betrachten musst. Wenn man den Worker Ordner kopiert bzw. klont, kann man das mittels IntelliJ (Oder Eclipse) öffnen und einfach mit "Run" Button starten. Es kann sein, dass man davor erst mal den SDK Setup ausführen muss (Wird bei IntelliJ oben Rechts automatisch vorgeschlagen).

Wichtig: Für einen lokalen test, sollte man erstmals in der Klasse SchedulerAPI die IPAdressen und den Pfad der UUID Text Datei ändern (Je nach dem wie es bei euch sein soll).
