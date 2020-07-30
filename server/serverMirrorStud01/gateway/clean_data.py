import mysql.connector
import random
import string
from datetime import datetime, timedelta
mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="HakunaMatata"
        database="sensordata",

)
sql = "SELECT * FROM TestData"
print(sql)
mycursor = mydb.cursor()

mycursor.execute(sql)
output = mycursor.fetchall()
mycursor.close()
print(output)
mydb.commit()

listOfRooms = []
for element in output:
        if element[0] not in listOfRooms:
                listOfRooms.append(element[0])

today = datetime.today()

mycursor = mydb.cursor()




yesterday = today - timedelta(days=1)
for room in listOfRooms:
        sql = "SELECT * FROM TestData WHERE TimeCategory = 'hour' AND Room = '" + room + "' AND Time BETWEEN '" + today.strftime('%Y-%m-%d %H:%M:%S') + "' AND '" + yesterday.strftime('%Y-%m-%d %H:%M:%S') + "';"
        print(sql)
        mycursor.execute(sql)
        output = mycursor.fetchall()
        if len(output) > 0:
                avgTemp = sum(output[1])/len(output)
                avgHum = sum(output[1])/len(output)
                avgNoise = sum(output[1])/len(output)
                avgWlan = sum(output[1])/len(output)
                sql = "INSERT INTO testdata Values ('%s',%d,%d,%d,%d,UTC_TIMESTAMP(),'%s')"%(room,avgTemp,avgHum,avgNoise,avgWlan,'day')
                print(sql)
                mycursor = mydb.cursor()
                mycursor.execute(sql)
                mydb.commit()
                sql = "DELETE * FROM TestData WHERE TimeCategory = 'hour' AND Room = '" + room + "' AND Time BETWEEN '" + today.strftime('%Y-%m-%d %H:%M:%S') + "' AND '" + yesterday.strftime('%Y-%m-%d %H:%M:%S') + "';"
                print(sql)
                mycursor = mydb.cursor()
                mycursor.execute(sql)
                mydb.commit()                       
mycursor.close()