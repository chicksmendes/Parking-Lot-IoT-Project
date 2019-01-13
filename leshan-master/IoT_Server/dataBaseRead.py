import sqlite3
import sys
import threading

# ----------------- FUNCTION ----------------
def printDB():
    threading.Timer(5.0, printDB).start()
    try:
        result = theCursor.execute("SELECT * FROM PARK ENDPOINT, STATE, PLATE")
        
        for row in result:
            print("ENDPOINT: ", row[0])
            print("STATE: ", row[1])
            print("PLATE: ", row[2])

    except sqlite3.OperationalError:
        print("The Table Doesn't Exist")

    except:
        print("DataBase Empty")

# ------------- END OF FUNCTIONS -------------

db_conn = sqlite3.connect('ParkingLotDB')

print("DataBase ParkingLotDB Opened")

printDB()
