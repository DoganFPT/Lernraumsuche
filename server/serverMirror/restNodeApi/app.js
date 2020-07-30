const express = require('express');
const app = express();
const mysql = require('mysql')

app.get("/", (req, res) =>{ 
    console.log("Responding to root route");
    res.send("Hello from rooooooot");
});

app.get("/sensorData", (req, res) =>{
    const sensorData1 = {room: "LZ122", temperature: 30, humidity: 100, wireless: 75, noise: 80};
    const sensorData2 = {room: "BB100", temperature: 30, humidity: 100, wireless: 75, noise: 80};
    const sensorData3 = {room: "CC444", temperature: 30, humidity: 100, wireless: 75, noise: 80};
    res.json([sensorData1,sensorData2,sensorData3]);
    
});

app.get("/databaseData", (req, res) =>{
    
    const connection = mysql.createConnection({
    	host: 'localhost',
    	user: 'root',
    	password: 'HakunaMatata',
    	database: 'sensordata',
	timezone: 'utc',
	dateStrings: true,	
    })
    
    connection.query("Select * From TestData", (err,rows,fields)=>{
    	console.log("fetched data jjuhuhuhuhu")
    	res.json(rows)
    })

});


module.exports = app;
