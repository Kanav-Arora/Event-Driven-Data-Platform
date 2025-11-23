from fastapi import FastAPI
from db import initiateDB
from createOrder import createOrder

app = FastAPI()

conn = initiateDB()

@app.post("/create-order")
def createOrderCaller():
    return createOrder(conn)
    