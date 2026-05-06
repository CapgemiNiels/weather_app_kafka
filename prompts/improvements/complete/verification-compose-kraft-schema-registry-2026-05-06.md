# Verification Plan - Compose KRaft + Schema Registry (`2026-05-06`)

This document defines the Phase 1 tests for the approved docker compose modernization work-order.

## Preconditions

- Docker Desktop (or Docker Engine + Compose plugin) is installed and running.
- Commands are executed from workspace root: `D:\Develop\GIT\weather_app_kafka`.
- PowerShell shell is used.

## Test Cases

### 1) ComposeConfig_hasNoZooKeeperReferences

**Goal**: Confirm ZooKeeper is fully removed.

```powershell
Get-Content .\docker-compose.yml | Select-String -Pattern "zookeeper" -CaseSensitive:$false
```

**Expected result**:
- No `zookeeper` service key present.
- No ZooKeeper env keys/references in any service.
- Command prints no matches.

---

### 2) KafkaService_usesApacheKafka402_andKRaftSettings

**Goal**: Confirm Kafka image and core KRaft settings are present.

```powershell
Get-Content .\docker-compose.yml | Select-String -Pattern "apache/kafka:4.0.2"
Get-Content .\docker-compose.yml | Select-String -Pattern "KAFKA_PROCESS_ROLES"
Get-Content .\docker-compose.yml | Select-String -Pattern "KAFKA_NODE_ID"
Get-Content .\docker-compose.yml | Select-String -Pattern "KAFKA_CONTROLLER_QUORUM_VOTERS"
Get-Content .\docker-compose.yml | Select-String -Pattern "KAFKA_CONTROLLER_LISTENER_NAMES"
```

**Expected result**:
- All five checks return matching lines.
- No ZooKeeper-dependent Kafka settings remain.

---

### 3) KafkaListeners_expose9092_andUseInternal29092

**Goal**: Confirm listener model and connectivity ports.

```powershell
Get-Content .\docker-compose.yml | Select-String -Pattern "9092:9092"
Get-Content .\docker-compose.yml | Select-String -Pattern "29092"
docker compose up -d
docker compose ps
```

**Expected result**:
- Compose contains host mapping `9092:9092`.
- Compose contains internal listener using `29092`.
- Broker is running after `docker compose up -d`.

---

### 4) KafkaUi_usesLatestImage_andBindsHost8085

**Goal**: Confirm UI image policy and host exposure.

```powershell
Get-Content .\docker-compose.yml | Select-String -Pattern "provectuslabs/kafka-ui:latest"
Get-Content .\docker-compose.yml | Select-String -Pattern "8085:8080"
Invoke-WebRequest -UseBasicParsing http://localhost:8085 | Select-Object -ExpandProperty StatusCode
```

**Expected result**:
- Image line matches `provectuslabs/kafka-ui:latest`.
- Port line matches `8085:8080`.
- HTTP status code returns `200` (or another successful 2xx/3xx response).

---

### 5) SchemaRegistry_serviceExists_andBindsHost8081

**Goal**: Confirm Schema Registry service and endpoint exposure.

```powershell
Get-Content .\docker-compose.yml | Select-String -Pattern "confluentinc/cp-schema-registry:latest"
Get-Content .\docker-compose.yml | Select-String -Pattern "8081:8081"
Invoke-WebRequest -UseBasicParsing http://localhost:8081/subjects | Select-Object -ExpandProperty StatusCode
```

**Expected result**:
- Image line matches `confluentinc/cp-schema-registry:latest`.
- Port line matches `8081:8081`.
- `/subjects` returns HTTP `200` with JSON list payload (possibly empty array).

---

### 6) SchemaRegistry_canReachKafkaBootstrap

**Goal**: Confirm Schema Registry bootstraps to Kafka without connectivity errors.

```powershell
docker compose logs schema-registry --tail=200
```

**Expected result**:
- Logs show successful startup.
- No repeated bootstrap connection failures to broker.

---

### 7) KafkaSmoke_canProduceAndConsumeSingleMessage

**Goal**: Confirm end-to-end produce/consume against broker.

```powershell
docker compose exec broker /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic smoke-test --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
docker compose exec -T broker sh -c "echo hello-smoke | /opt/kafka/bin/kafka-console-producer.sh --topic smoke-test --bootstrap-server localhost:9092"
docker compose exec broker /opt/kafka/bin/kafka-console-consumer.sh --topic smoke-test --bootstrap-server localhost:9092 --from-beginning --timeout-ms 5000 --max-messages 1
```

**Expected result**:
- Topic creation succeeds (or reports already exists).
- Producer command exits normally.
- Consumer prints `hello-smoke` and exits.

---

### 8) ComposeRestart_preservesKafkaDataWithVolume

**Goal**: Confirm persisted state survives container restart.

```powershell
docker compose exec broker /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic persist-check --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
docker compose down
docker compose up -d
docker compose exec broker /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 | Select-String -Pattern "persist-check"
```

**Expected result**:
- Topic `persist-check` exists before restart.
- After down/up, topic still appears in topic list.
- Confirms volume-backed metadata/log persistence.

---

## Cleanup

```powershell
docker compose down
```

Optional full cleanup including volumes (destructive):

```powershell
docker compose down -v
```


