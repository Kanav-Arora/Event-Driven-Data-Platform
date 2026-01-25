from models.RejectOrderModel import RejectOrderModel
import uuid
import json

def rejectOrder(conn, request: RejectOrderModel):
    try:
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    UPDATE orders SET order_status = 'REJECTED'
                    where order_id = %s
                    """, (request.order_id,)
                )
                event_metadata = json.dumps({
                            "rejection_source": request.rejection_source
                        })
                
                cursor.execute(
                    """
                    INSERT INTO events (
                        event_id,
                        aggregate_type,
                        aggregate_id,
                        event_type,
                        payload,
                        metadata,
                        created_at
                    )
                    VALUES (%s, %s, %s, %s, %s, %s,%s)
                    """,
                    (
                        str(uuid.uuid4()),
                        "orders",
                        request.order_id,
                        "reject-order",
                        json.dumps(request.payload.dict()),
                        event_metadata,
                        request.timestamp
                    )
                )

                conn.commit()
                return {"status":200, "message": "Success", "payload": request.payload}
        
    except Exception as e:
        conn.rollback()
        print(f"Error rejecting order: {e}")
        return {"status":400, "message": e}