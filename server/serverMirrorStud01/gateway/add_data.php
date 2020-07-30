
<?php
$mysqli = new mysqli("localhost", "root", "HakunaMatata", "sensordata");
if ($mysqli->connect_errno) {
    die("Verbindung fehlgeschlagen: " . $mysqli->connect_error);
}
 
$sql = "INSERT INTO TestData Values ('GG130',10,20,30,-80,UTC_TIMESTAMP())";
$mysqli->query($sql);
?>
