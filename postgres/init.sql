CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS customers (
    cust_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cust_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    country VARCHAR(50),
    region VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_name VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
    inventory_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID NOT NULL REFERENCES products(product_id),
    warehouse_id    UUID NOT NULL REFERENCES warehouses(warehouse_id),
    quantity INT NOT NULL,
    updated_at      TIMESTAMP DEFAULT NOW(),
    UNIQUE (product_id, warehouse_id)
);

CREATE TABLE inventory_events (
    event_id BIGSERIAL PRIMARY KEY,
    inventory_id UUID NOT NULL REFERENCES inventory(inventory_id),
    event_type TEXT NOT NULL CHECK (event_type IN ('SALE', 'RESTOCK','RETURN','DISCONTINUE')),
    quantity_change INT NOT NULL,
    timestamp TIMESTAMP DEFAULT NOW()
);
