===== Smarte Lernraumsuche im IoT =====
Die config-File "SLIOT_CONFIG.cfg" muss sich im gleichen Ordner, wie die ausführende .jar-Datei befinden.
Sind Werte in der Konfigurationsdatei nicht belegt, werden (falls vorhanden) die default-Werte genutzt.
Das Programm für den Raspberry Pi ist erst auszuführen, wenn der Gateway-Argon angeschlossen und komplett hochgefahren ist. Bei Nicht-Einhalten muss das Programm neugestartet werden.

--- defaults ---
ROOM_FILE=/home/<username>/Documents/sliot/rooms.json
FALSE_CODE=666			(Float-Wert)
SERVER_URL=https://stud01.vs.uni-due.de/gateway/add_data_custom.php
SENDING_INVTERVAL=15000 (Zeit in ms)
