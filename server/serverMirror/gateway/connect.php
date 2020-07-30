<?php

$MyUsername = "root";
$MyPassword = "HakunaMatata";
$MyHostname = "localhost";

$dbh = mysql_pconnect($MyHostname, $MyUsername, $MyPassword);
$selected = mysql_select_db("sensordata",$dbh);

?>
