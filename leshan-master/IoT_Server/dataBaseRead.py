import sqlite3
import sys
import threading
import sched, time

# ----------------- FUNCTION ----------------
def printDB(sc):
    c = db_conn.cursor()
    result = c.execute("SELECT * FROM park")
    result = c.fetchone()
    
    i = 1
    while result != None:
        print(i, ":", result)
        result = c.fetchone()
        i = i + 1
    print("---")
    
    s.enter(5, 1, printDB, (sc,))
    

# ------------- END OF FUNCTIONS -------------

db_conn = sqlite3.connect('ParkingLotDB.db')

print("DataBase ParkingLotDB Opened")

s = sched.scheduler(time.time, time.sleep)

s.enter(1, 1, printDB, (s,))
s.run()

