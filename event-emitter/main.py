from fastapi import FastAPI
from db import initiateDB
from endpoints.createOrder import createOrder
from endpoints.rejectOrder import rejectOrder
from models.RejectOrderModel import RejectOrderModel

app = FastAPI()

conn = initiateDB()

@app.post("/create-order")
def createOrderCaller():
    return createOrder(conn)

@app.post("/reject-order")
def rejectOrderCaller(request: RejectOrderModel):
    return rejectOrder(conn,request)
    