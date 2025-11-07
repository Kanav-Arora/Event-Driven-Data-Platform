from faker import Faker
import psycopg2
from dotenv import load_dotenv
import os

fake = Faker()

customers = []
products = []
warehouses = []
inventory = []

for _ in range(100):
    customer = {
        "cust_name": fake.name().title(),
        "cust_email": fake.email(),
        "cust_address": fake.address().replace("\n", ", "),
        "cust_city": fake.city(),
        "cust_country": fake.country()
    }
    customers.append(customer)

for _ in range(50):
    product = {
        "product_name": fake.word().title(),
        "product_description": fake.sentence(nb_words=10),
        "category": fake.word().title(),
        "price": round(fake.random_number(digits=4) / 100, 2)
    }
    products.append(product)

for _ in range(10):
    warehouse = {
        "warehouse_name": fake.company(),
        "warehouse_city": fake.city(),
        "warehouse_country": fake.country()
    }
    warehouses.append(warehouse)

load_dotenv()

conn = psycopg2.connect(
    dbname=os.getenv("POSTGRES_DB"),
    user=os.getenv("POSTGRES_USER"),
    password=os.getenv("POSTGRES_PASSWORD"),
    host=os.getenv("POSTGRES_HOST_INTERNAL"),
    port=os.getenv("POSTGRES_PORT")
)
cursor = conn.cursor()

# Populating Customers Table
for _ in customers:
    try:
        cursor.execute(
            """
            INSERT INTO customers 
            (cust_name, cust_email, cust_address, cust_city, cust_country)
            VALUES (%s, %s, %s, %s, %s)
            """,
            (
                _["cust_name"],
                _["cust_email"],
                _["cust_address"],
                _["cust_city"],
                _["cust_country"]
            )
        )
        conn.commit()
    except Exception as e:
        print(f"Skipping record {_}: {e}")
        conn.rollback()

# Populating Products Table
for _ in products:
    try:
        cursor.execute(
            """
            INSERT INTO products 
            (product_name, product_description, category, price)
            VALUES (%s, %s, %s, %s)
            """,
            (
                _["product_name"],
                _["product_description"],
                _["category"],
                _["price"]
            )
        )
        conn.commit()
    except Exception as e:
        print(f"Skipping record {_}: {e}")
        conn.rollback()

# Populating Warehouses Table
for _ in warehouses:
    try:
        cursor.execute(
            """
            INSERT INTO warehouses 
            (warehouse_name, warehouse_city, warehouse_country)
            VALUES (%s, %s, %s)
            """,
            (
                _["warehouse_name"],
                _["warehouse_city"],
                _["warehouse_country"]
            )
        )
        conn.commit()
    except Exception as e:
        print(f"Skipping record {_}: {e}")
        conn.rollback()

# Populating Inventory and Inventory Events Tables
cursor.execute("""SELECT product_id, warehouse_id from 
               (SELECT product_id from products) AS p 
               CROSS JOIN (SELECT warehouse_id from warehouses) AS w
               """)

rows = cursor.fetchall()

for row in rows:
    product_id, warehouse_id = row
    quantity = fake.random_int(min=0, max=10000)
    try:
        cursor.execute(
            """
            INSERT INTO inventory 
            (product_id, warehouse_id, quantity)
            VALUES (%s, %s, %s)
            returning inventory_id;
            """,
            (
                product_id,
                warehouse_id,
                quantity
            )
        )

        inventory_id = cursor.fetchone()[0]

        cursor.execute(
                """
                INSERT INTO inventory_events 
                (inventory_id, event_type, quantity_change)
                VALUES (%s, %s, %s)
                """,
                (
                    inventory_id,
                    "RESTOCK",
                    quantity
                )
            )
        conn.commit()
    except Exception as e:
        print(f"Skipping record {row}: {e}")
        conn.rollback()

conn.close()