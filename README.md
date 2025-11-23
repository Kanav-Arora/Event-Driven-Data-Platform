### Event Driven Data Platform

| Service                                | Description                             | Internal Port | Host Port (localhost) | Access URL / Notes                                       |
| -------------------------------------- | --------------------------------------- | ------------- | --------------------- | -------------------------------------------------------- |
| 🐘 **PostgreSQL**                      | Primary database (truth data source)    | `5432`        | `5432`                | `postgresql://localhost:5432`                            |
| 🐳 **FastAPI (Order & Inventory API)** | REST API for order/inventory events     | `8000`        | `8000`                | [http://localhost:8000/docs](http://localhost:8000/docs) |
| ⚡ **Kafka Broker**                     | Event streaming backbone                | `9092`        | `9092`                | Broker: `localhost:9092`                                 |
| 🦓 **Zookeeper**                       | Kafka cluster coordinator               | `2181`        | `2181`                | Internal only (Kafka dependency)                         |
| 🔁 **Debezium Connector**              | CDC from PostgreSQL → Kafka             | `8083`        | `8083`                | REST API: `http://localhost:8083/connectors`             |
| 🪶 **Flink JobManager**                | Stream processing engine (web UI)       | `8081`        | `8081`                | [http://localhost:8081](http://localhost:8081)           |
| ⚙️ **Flink TaskManager**               | Executes Flink jobs                     | —             | —                     | Internal worker; no direct port                          |
| 📈 **Grafana**                         | Metrics & dashboards                    | `3000`        | `3000`                | [http://localhost:3000](http://localhost:3000)           |
| 📜 **Loki**                            | Centralized logging backend             | `3100`        | `3100`                | Endpoint: `http://localhost:3100`                        |
| 🪵 **Promtail**                        | Log collector (Docker + FastAPI → Loki) | `9080`        | `9080`                | Usually internal only                                    |
| 🧰 **Schema Registry**      | For Avro serialization with Kafka       | `8081`        | `8081`                | `http://localhost:8081`                                  |
