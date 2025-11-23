import psycopg2
import os

def initiateDB():
    conn = psycopg2.connect(
        dbname=os.getenv("POSTGRES_DB"),
    user=os.getenv("POSTGRES_USER"),
    password=os.getenv("POSTGRES_PASSWORD"),
    host=os.getenv("POSTGRES_HOST_INTERNAL"),
    port=os.getenv("POSTGRES_PORT")
    )
    return conn