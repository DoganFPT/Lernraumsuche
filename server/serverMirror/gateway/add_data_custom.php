
<?php
$mysqli = new mysqli("localhost", "root", "HakunaMatata", "sensordata");
if ($mysqli->connect_errno) {
    die("Verbindung fehlgeschlagen: " . $mysqli->connect_error);
}
 
$sql = "INSERT INTO TestData Values (".$_GET["room"].",".$_GET["temp"].",".$_GET["hum"].",".$_GET["noise"].",".$_GET["wlan"].",UTC_TIMESTAMP())";
print($sql);
$mysqli->query($sql);
?>
