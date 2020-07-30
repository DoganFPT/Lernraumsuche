
import mysql.connector
import random
import string

mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="HakunaMatata",
        database="sensordata",

)

rooms = ['LF031','BB100','LC202']

for room in rooms:

        temperature = random.randint(0,30)
        humidity = random.randint(0,100)
        noise = random.randint(0,100)
        wlan = random.randint(0,100)

        sql = "SELECT * FROM RandomData"
        print(sql)
        mycursor = mydb.cursor()

        result = mycursor.execute(sql)
        mydb.commit()