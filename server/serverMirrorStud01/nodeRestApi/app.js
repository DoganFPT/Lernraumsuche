const express = require('express');
const app = express();
const mysql = require('mysql')
const util = require('util');



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
    
    var gNumOfRooms = 0;

    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'HakunaMatata',
        database: 'sensordata',
    timezone: 'utc',
    dateStrings: true,  
    })
    
    // get number of rooms

    connection.query("select distinct Room from TestData", (err,rooms,fields)=>{
        var roomNum = rooms.length;

    connection.query("select * from TestData order by time desc limit "+roomNum*3, (err,rows,fields)=>{
    //connection.query("select room, group_concat(concat(noise,' ,',time)) as noise from TestData group by Room order by room", (err,rows,fields)=>{
    //console.log(rows)       
        console.log(rooms.length)

        //sortJson
        const sortedReal = rows.reduce((result, data) => {
            const a = result.find(({Room}) => Room === data.Room);
            a ? a.data.push(data) : result.push({Room: data.Room, data: [data]});

            return result;
        }, []);

        //remove unnecessary room entries
        for (i = 0; i < sortedReal.length; i++) {
            for(j=0; j<sortedReal[i]['data'].length; j++){
                delete sortedReal[i]['data'][j].Room
            }
        }

        //delete sortedReal[0]['data'][0].Room
        app.set('json spaces', 50);
        res.send(sortedReal);
        
        
        })

    })

});

app.get("/randomData", (req, res) =>{
    
    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'HakunaMatata',
        database: 'sensordata',
    timezone: 'utc',
    dateStrings: true,  
    })
    
    connection.query("Select * From randomData", (err,rows,fields)=>{
        console.log("fetched data jjuhuhuhuhu")
        res.json(rows)
    })

});


app.get("/random24", (req, res) =>{
    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'HakunaMatata',
        database: 'sensordata',
    timezone: 'utc',
    dateStrings: true,  
    })
    
    

    connection.query("SELECT * FROM randomData  WHERE Time >= now() - INTERVAL 1 DAY ORDER BY room", (err,rows,fields)=>{
        res.json(rows)
    })    
});



app.get("/lf031Random", (req, res) =>{
    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'HakunaMatata',
        database: 'sensordata',
    timezone: 'utc',
    dateStrings: true,  
    })
    
    connection.query("SELECT * FROM randomData  WHERE Time >= now() - INTERVAL 1 DAY and room= 'lf031' ", (err,rows,fields)=>{
        console.log("fetched data jjuhuhuhuhu")
        res.json(rows)
    })
});

app.get("/bb100Random", (req, res) =>{
    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'HakunaMatata',
        database: 'sensordata',
    timezone: 'utc',
    dateStrings: true,  
    })
    
    connection.query("SELECT * FROM randomData  WHERE Time >= now() - INTERVAL 1 DAY and room= 'bb100' ", (err,rows,fields)=>{
        console.log("fetched data jjuhuhuhuhu")
        res.json(rows)
    })
});

app.get("/lc202Random", (req, res) =>{
    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'HakunaMatata',
        database: 'sensordata',
    timezone: 'utc',
    dateStrings: true,  
    })
    
    connection.query("SELECT * FROM randomData  WHERE Time >= now() - INTERVAL 1 DAY and room= 'lc202' ", (err,rows,fields)=>{
        console.log("fetched data jjuhuhuhuhu")
        res.json(rows)
    })
});




module.exports = app;
