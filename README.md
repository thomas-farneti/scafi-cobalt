# ⚡️ Scafi Cobalt

**Scafi Cobalt** is a powerful Scala-based framework for building **distributed cloud architectures** using **aggregate programming** principles. Inspired by **field calculus**, this project is designed to orchestrate complex, resilient, and self-organizing systems across distributed nodes — whether in the cloud, at the edge, or on devices.

> “Program the collective, not the device.” — Field Programming Philosophy

---

## 🚀 What is Scafi Cobalt?

Scafi Cobalt brings together the power of **[Scafi](https://scafi.github.io/)** — a Scala DSL for aggregate computing — with a modular service-based architecture, enabling:

- 🌐 Cloud-native deployment of field-based computations
- 🔁 Continuous, distributed execution of Scafi programs
- 🧠 Sensor data integration and ingestion services
- 📊 Real-time visualizations of distributed field computations
- 📦 Docker-ready microservices for seamless deployment

It’s not just a toolkit — it’s a playground for researchers, developers, and tinkerers working on decentralized intelligence.

---

## 🧱 Architecture Overview

The system is composed of loosely coupled services:

```
+------------------------+     +-------------------------+
|   Ingestion Service    | --> |    Execution Service    |
+------------------------+     +-------------------------+
         |                             |
         v                             v
+----------------+          +--------------------------+
| Sensors Module |          | Field Visualizer Service |
+----------------+          +--------------------------+
         |
         v
+-----------------------+
|   Domain Logic Core   |
+-----------------------+
```

Each module is implemented in **Scala**, built with **SBT**, and containerized for cloud deployments.

---

## 🛠️ Technologies Used

- 🐍 **Scafi** – Aggregate programming DSL
- 🧪 **Scala** – Functional & scalable foundation
- 🐳 **Docker** – Containerized microservices
- 🛠 **SBT** – Build tool for Scala
- ☁️ Designed for cloud-native deployment

---

## 🔧 Getting Started

### Prerequisites

- JDK 11+
- [SBT](https://www.scala-sbt.org/download.html)
- Docker (optional for containerized services)

### Build & Run Locally

```bash
git clone https://github.com/thomas-farneti/scafi-cobalt.git
cd scafi-cobalt
sbt compile
sbt run
```

### Run with Docker

```bash
cd docker
docker compose up --build
```

---

## 🧪 Services Breakdown

- **common/** – Shared utilities and base traits
- **domainService/** – Core business logic and model definitions
- **executionService/** – Executes field programs across distributed nodes
- **fieldVisualizerService/** – Provides UI for field visualization
- **ingestionService/** – Manages incoming sensor data
- **sensorsService/** – Virtual/physical sensor integration
- **testDevice/** – Simulated nodes for local testing

---

## 🌍 Why Scafi Cobalt?

- 🧬 **Inspired by Nature**: Program like biological systems — local rules, global behaviors
- 🌩 **Cloud-ready**: Designed for scalability and modularity
- 🎓 **Research-grade**: Ideal for prototyping novel distributed computing models
- 🕹 **Interactive & Visual**: View your fields in action

---

## 🤝 Contributing

We welcome contributions from developers and researchers alike. To get involved:

1. Fork this repo
2. Create a new branch: `git checkout -b feature/your-feature`
3. Submit a pull request with clear description and motivation

Feel free to open issues or discussions for questions, ideas, or feedback.

---

## 📜 License

**MIT License** – see [`LICENSE`](./LICENSE) file for details.

---

## 📡 Connect

Created by [@thomas-farneti](https://github.com/thomas-farneti) • Inspired by aggregate computing & the Scafi ecosystem.

---

> “From sensor to cloud — Scafi Cobalt programs the collective.”
