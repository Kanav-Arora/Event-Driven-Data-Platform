from pydantic import BaseModel
from datetime import datetime
from typing import Dict,List

class OrderItem(BaseModel):
    inventory_id: str
    quantity: int

class RejectOrderPayload(BaseModel):
    order_id: str
    order_items: List[OrderItem]

class RejectOrderModel(BaseModel):
    order_id: str
    payload: RejectOrderPayload
    rejection_source: str
    timestamp: datetime

